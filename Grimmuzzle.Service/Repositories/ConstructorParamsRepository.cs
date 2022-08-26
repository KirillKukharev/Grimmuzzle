using System;
using System.IO;
using System.Text;
using System.Threading.Tasks;
using Grimmuzzle.Service.Interfaces;

namespace Grimmuzzle.Service.Repositories
{
    public class ConstructorParamsRepository : IAsyncConstructorParamsRepository
    {
        public Task<string> GetConstructorParamsAsync()
        {
            var constructorPath = Path.Combine(AppContext.BaseDirectory, "fairyTaleConstructor.json");
            return File.ReadAllTextAsync(constructorPath, Encoding.UTF8);
        }
    }
}