using System.Threading.Tasks;

namespace Grimmuzzle.Service.Interfaces
{
    public interface IAsyncConstructorParamsRepository
    {
        Task<string> GetConstructorParamsAsync();
    }
}