using Azure.Messaging.ServiceBus;
using Grimmuzzle.Service.DTOs;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using System.Text;
using System.Threading.Tasks;

namespace Grimmuzzle.Service.Queue
{
    /// <summary>
    /// todo
    /// </summary>
    public class RequestsQueue : IRequestsQueue
    {
        private readonly string _connectionString;
        private readonly string _queueName;

        public RequestsQueue(IOptions<RequestsQueueConnectionConfiguration> configurationOptions)
        {
            _connectionString = configurationOptions.Value.connectionString;
            _queueName = configurationOptions.Value.queueName;
        }


        public async Task SendInputParametersToQueue(FairyTaleDto dto)
        {
            await using ServiceBusClient client = new ServiceBusClient(_connectionString);
            ServiceBusSender sender = client.CreateSender(_queueName);

            ServiceBusMessage message = new ServiceBusMessage(JsonConvert.SerializeObject(dto));

            await sender.SendMessageAsync(message);
        }

        public async Task<FairyTaleDto> ReceiveMessageFromQueue()
        {
            await using ServiceBusClient client = new ServiceBusClient(_connectionString);
            ServiceBusReceiver receiver = client.CreateReceiver(_queueName);
            ServiceBusReceivedMessage receivedMessage = await receiver.ReceiveMessageAsync();
            if (receivedMessage == null) return null;
            await receiver.CompleteMessageAsync(receivedMessage);
            var body = receivedMessage.Body;
            FairyTaleDto dto = (FairyTaleDto) JsonConvert.DeserializeObject(Encoding.ASCII.GetString(body), typeof(FairyTaleDto));
            return dto;
        }

        public async Task ReceiveAndDeleteDeadLetters()
        {
            await using ServiceBusClient client = new ServiceBusClient(_connectionString);
            ServiceBusReceiver dlqReceiver = client.CreateReceiver(_queueName, new ServiceBusReceiverOptions
            {
                SubQueue = SubQueue.DeadLetter
            });

            while (true)
            {
                ServiceBusReceivedMessage message = await dlqReceiver.ReceiveMessageAsync();
                if (message == null) break;
                await dlqReceiver.CompleteMessageAsync(message);
            }
        }

    }
}
