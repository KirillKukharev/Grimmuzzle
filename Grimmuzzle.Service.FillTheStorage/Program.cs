using Azure.Messaging.ServiceBus;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading.Tasks;

namespace CheckMLConnection
{
    class Program    {

        private static readonly int length = 250;
        private static readonly int numberOfIdenticalAttributes = 1;

        private static readonly string connectionString = "Endpoint=sb://grimmuzzle.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=ThMafwezT48c3GLsRagd+g+qHfskaHNJFjyyz6VNj5w=";
        private static readonly string queueName = "grimmuzzlequeue";

        private static int whoCount;
        private static int whatCount;
        private static int whenCount;
        private static int whereCount;

        private static JArray whoItems;
        private static JArray whatItems;
        private static JArray whenItems;
        private static JArray whereItems;

        private static readonly List<string> whoResult = new List<string>();
        private static readonly List<string> whatWhenWhereResult = new List<string>();
        private static readonly List<string> generalResult = new List<string>();

        static async Task Main(string[] args)
        {
            var constructorPath = Path.Combine(AppContext.BaseDirectory, "fairyTaleConstructor.json");
            string constructor = await File.ReadAllTextAsync(constructorPath, Encoding.UTF8);
            JObject desConstructor = JsonConvert.DeserializeObject<JObject>(constructor);

            whoItems = (JArray)((JObject)desConstructor.GetValue("Who")).GetValue("items");
            whatItems = (JArray)((JObject)desConstructor.GetValue("What")).GetValue("items");
            whenItems = (JArray)((JObject)desConstructor.GetValue("When")).GetValue("items");
            whereItems = (JArray)((JObject)desConstructor.GetValue("Where")).GetValue("items");

            whoCount = whoItems.Count;
            whatCount = whatItems.Count;
            whenCount = whenItems.Count;
            whereCount = whereItems.Count;

            СreateArrayWithAttributes();

            //TODO: в generalResult - итоговый список атрибутов для отправки в МЛ или в очередь

            //await SendGeneralResultToQueue();

            //await DeleteAllMessageFromQueue();
        }

        private static void СreateArrayWithAttributes() 
        {
            List<int> whoOptions = new List<int>();
            GenWhoStringList(0, whoOptions);

            GenWhatWhenWhereStringList();

            GenGeneralResult();
        }

        private static void GenWhoStringList(int curId, List<int> curSequence)
        {
            if (curId == whoCount)
            {
                if(curSequence.Count != 0)
                    whoResult.Add("\"Who\":" + JsonConvert.SerializeObject(curSequence));
            }
            else
            {
                GenWhoStringList(curId + 1, curSequence);
                curSequence.Add((int)((JObject)whoItems[curId]).GetValue("id"));
                GenWhoStringList(curId + 1, curSequence);
                curSequence.RemoveAt(curSequence.Count - 1);
            }
        }

        private static void GenWhatWhenWhereStringList()
        {
            for (int i = 0; i < whatCount; i++) 
            {
                for (int j = 0; j < whenCount; j++)
                {
                    for (int k = 0; k < whereCount; k++)
                    {
                        whatWhenWhereResult.Add(String.Format(",\"What\":[{0}],\"When\":[{1}],\"Where\":[{2}]",
                            (int)((JObject)whatItems[i]).GetValue("id"),
                            (int)((JObject)whenItems[j]).GetValue("id"),
                            (int)((JObject)whereItems[k]).GetValue("id")));
                    }
                }
            }
        }

        private static void GenGeneralResult()
        {
            for(int i = 0; i < whoResult.Count; i++)
            {
                for(int j = 0; j < whatWhenWhereResult.Count; j++)
                {
                    generalResult.Add("{" + whoResult[i] + whatWhenWhereResult[j] + "}");
                }
            }
        }

        private static async Task SendGeneralResultToQueue()
        {
            await using ServiceBusClient client = new ServiceBusClient(connectionString);
            ServiceBusSender sender = client.CreateSender(queueName);

            for (int i = 0; i < numberOfIdenticalAttributes; i++)
            {
                for (int j = 0; j < generalResult.Count; j++)
                {
                      ServiceBusMessage message = new ServiceBusMessage(String.Format("{{\"Input\":{0},\"Length\":{1}}}", generalResult[j], length));
                      await sender.SendMessageAsync(message);
                }    
            }
        }

        private static async Task DeleteAllMessageFromQueue()
        {
            while (true)
            {
                await using ServiceBusClient client = new ServiceBusClient(connectionString);
                ServiceBusReceiver receiver = client.CreateReceiver(queueName);
                ServiceBusReceivedMessage receivedMessage = await receiver.ReceiveMessageAsync();
                if (receivedMessage == null) break;
                await receiver.CompleteMessageAsync(receivedMessage);
            }
        }
    }
}
