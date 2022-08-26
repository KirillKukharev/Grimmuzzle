using Grimmuzzle.Service.Interfaces;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using NCrontab;
using System;
using System.Threading;
using System.Threading.Tasks;

namespace Grimmuzzle.Service.Queue
{
    public class DeadLetterQueueHostedService : BackgroundService
    {
        private CrontabSchedule _schedule;
        private DateTime _nextRun;
        private readonly IServiceScopeFactory _scopeFactory;

        private string Schedule => "* * */4 * * *"; //Runs every 4 hours

        public DeadLetterQueueHostedService(IServiceScopeFactory scopeFactory)
        {
            _scopeFactory = scopeFactory;
            _schedule = CrontabSchedule.Parse(Schedule, new CrontabSchedule.ParseOptions { IncludingSeconds = true });
            _nextRun = _schedule.GetNextOccurrence(DateTime.Now);
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
                await Task.Delay(20 * 60 * 1000, stoppingToken); //20 minutes delay
            }
            while (!stoppingToken.IsCancellationRequested);
        }

        private async Task Process()
        {
            try
            {
                using (var scope = _scopeFactory.CreateScope())
                {
                    var queue = scope.ServiceProvider.GetRequiredService<IRequestsQueue>();
                    await queue.ReceiveAndDeleteDeadLetters();                    
                }
            }
            catch (Exception e) { Console.WriteLine(e.Message); }
        }
    }
}
