using Grimmuzzle.Service.Interfaces;
using Microsoft.AspNetCore.Mvc;
using System.Threading.Tasks;

namespace Grimmuzzle.Service.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class TaleConstructorParamsController : ControllerBase
    {
        private readonly IAsyncConstructorParamsRepository _constructorParamsRepository;

        public TaleConstructorParamsController(IAsyncConstructorParamsRepository paramsRepository)
        {
            _constructorParamsRepository = paramsRepository;
        }

        [HttpGet]
        public async Task<string> Get()
        {
            var constructor = await _constructorParamsRepository.GetConstructorParamsAsync();

            return constructor;
        }
    }
}