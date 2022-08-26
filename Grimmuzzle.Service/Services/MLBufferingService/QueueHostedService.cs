using Grimmuzzle.Service.Converters;
using Grimmuzzle.Service.DTOs;
using Grimmuzzle.Service.Interfaces;
using Grimmuzzle.Service.Models;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Options;
using NCrontab;
using System;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;

namespace Grimmuzzle.Service.Queue
{
    public class QueueHostedService : BackgroundService
    {
        private CrontabSchedule _schedule;
        private DateTime _nextRun;
        private readonly IServiceScopeFactory _scopeFactory;
        private readonly IHttpClientFactory _clientFactory;
        private readonly int _maxRequestsCount;
        private string Schedule => "*/2 * * * * *"; //Runs every 2 seconds

        public QueueHostedService(IServiceScopeFactory scopeFactory, IHttpClientFactory clientFactory,
            IOptions<FairyTaleGeneratorConfiguration> configurationOptions)
        {
            _scopeFactory = scopeFactory;
            _schedule = CrontabSchedule.Parse(Schedule, new CrontabSchedule.ParseOptions { IncludingSeconds = true });
            _nextRun = _schedule.GetNextOccurrence(DateTime.Now);
            _clientFactory = clientFactory;
            Counter = 0;
            _maxRequestsCount = configurationOptions.Value.MaxRequestsCount;
        }

        public static int Counter;

        public static void IncreaseCounter()
        {
            Interlocked.Increment(ref Counter);
        }

        public static void DecreaseCounter()
        {
            Interlocked.Decrement(ref Counter);
        }

        protected override async Task ExecuteAsync(CancellationToken stoppingToken)
        {
            do
            {
                var now = DateTime.Now;
                if (now > _nextRun)
                {
                    await Process();
                    _nextRun = _schedule.GetNextOccurrence(DateTime.Now);
                }
                await Task.Delay(2000, stoppingToken); //2 seconds delay
            }
            while (!stoppingToken.IsCancellationRequested);
        }

        private async Task Process()
        {
            using (var scope = _scopeFactory.CreateScope())
            {
                var queue = scope.ServiceProvider.GetRequiredService<IRequestsQueue>();

                if (Counter < _maxRequestsCount)
                {
                    for (int i = 0; i < _maxRequestsCount - Counter; i++)
                    {
                        FairyTaleDto message = await queue.ReceiveMessageFromQueue();
                        if (message == null) break;

                        RequestML(message).ConfigureAwait(false);
                        await Task.Delay(1000);
                    }
                }
            }
        }

        private async Task RequestML(FairyTaleDto dto)
        {
            try
            {
                using (var scope = _scopeFactory.CreateScope())
                {
                    var repository = scope.ServiceProvider.GetRequiredService<IAsyncFairyTaleRepository>();
                    var client = _clientFactory.CreateClient("ML");
                    var service = scope.ServiceProvider.GetRequiredService<IFairyTaleGeneratorService>();
                    try
                    {
                        IncreaseCounter();

                        await repository.Create(dto);
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine(e.Message);
                    }
                    finally
                    {
                        DecreaseCounter();
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}