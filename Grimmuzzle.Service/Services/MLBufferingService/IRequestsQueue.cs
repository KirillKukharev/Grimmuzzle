
using Grimmuzzle.Service.DTOs;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Grimmuzzle.Service.Queue
{
    public interface IRequestsQueue
    {
        Task SendInputParametersToQueue(FairyTaleDto id);
        Task<FairyTaleDto> ReceiveMessageFromQueue();
        Task ReceiveAndDeleteDeadLetters();
    }
}
