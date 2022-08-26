using Grimmuzzle.Service.Converters;
using Grimmuzzle.Service.DTOs;
using Grimmuzzle.Service.Interfaces;
using Grimmuzzle.Service.Models;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Grimmuzzle.Service.Controllers
{
    /// <summary>
    /// MVC controller which send requests to <see cref="IAsyncFairyTaleRepository"/>
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    public class FairyTaleController : ControllerBase
    {
        private readonly IAsyncFairyTaleRepository _repository;

        /// <summary>
        /// Create an MVC controller using <see cref="IAsyncFairyTaleRepository"/>
        /// </summary>
        /// <param name="repository"></param>
        public FairyTaleController(IAsyncFairyTaleRepository repository)
        {
            _repository = repository;
        }

        /// <summary>
        /// This GET method returns all fairy tales data from repository.
        /// </summary>
        /// <returns>Result code and an object.</returns>
        /// <response code="200">Success</response>
        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public async Task<ActionResult> Get()
        {
            IEnumerable<FairyTaleResponse> fairyTales = (await _repository.GetAll())
                .Select(FairyTaleDtoGetter.GetResponseDto)
                .ToList();

            return Ok(fairyTales);
        }

        /// <summary>
        /// This GET method returns fairy tale data by ID.
        /// </summary>
        /// <param name="id">ID of fairy tale.</param>
        /// <returns>Result code and an object.</returns>
        /// <response code="200">Success</response>
        /// <response code="404">Fairy tale cannot be found</response>
        [HttpGet("{id:guid}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult> Get(Guid id)
        {
            var fairyTale = await _repository.Get(id);

            if (fairyTale == null)
                return NotFound();

            var fairyTaleDto = FairyTaleDtoGetter.GetResponseDto(fairyTale);
            return Ok(fairyTaleDto);
        }

        /// <summary>
        /// This method can send fairy tale data into repository.
        /// </summary>
        /// <remarks>
        /// Sample request:
        ///
        ///     POST /api/FairyTale
        ///     {
        ///        "name": "New fairy tale",
        ///        "length": 100,
        ///        "input": {
        ///             "Who": [123],
        ///             "What": [1],
        ///             "When": [2],
        ///             "Where": [3]
        ///         }
        ///     }
        ///
        /// </remarks>
        /// <param name="fairyTaleDto">Fairy tale object DTO representation.</param>
        /// <returns>Result code and an object.</returns>
        /// <response code="201">
        /// Returns the newly created item
        /// <remarks>
        /// <para>Sample response:</para>
        /// 
        ///     {
        ///         "id": "02FCED3D-D6A1-4380-4B1C-08D91161A701",
        ///         "name": "Ft",
        ///         "text": "Fairy tale here",
        ///         "timestamp": "2000-03-01T00:00:00.000000+03:00",
        ///         "length": 228,
        ///         "input": {
        ///             "attributes": {
        ///                 "Who": [123],
        ///                 "What": [1],
        ///                 "When": [2],
        ///                 "Where": [3]
        ///             },
        ///             "generatedString": "Black cat Run Autumn rain Dread forest"
        ///         }
        ///     }
        /// 
        /// </remarks>
        /// </response>
        /// <response code="400">Returns when name or input are empty or null. Or length less or equals 0</response>
        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult> Post([FromBody] FairyTaleDto fairyTaleDto)
        {            
            var fairyTale = await _repository.GetFromPull(fairyTaleDto);
            if (fairyTale == null) return NotFound();

            var responseFt = FairyTaleDtoGetter.GetResponseDto(fairyTale);

            return Created($"/api/FairyTale/{responseFt.Id}", responseFt);
        }

        /// <summary>
        /// This POST method return fairy tale by user input text (EN or RU)
        /// </summary>
        /// <remarks>
        /// Sample request:
        ///
        ///     POST  /api/FairyTale/GetTalesFromStore/
        ///     {
        ///        "name": "New fairy tale",
        ///        "input": "User input string",
        ///        "length": 100
        ///     }
        ///
        /// </remarks>
        /// <param name="fairyTaleDto"></param>
        /// <returns></returns>
        [HttpPost("PostWithUserInput")]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult> PostWithUserInput([FromBody] FairyTaleWithUserInputDto fairyTaleDto)
        {
            if(String.IsNullOrEmpty(fairyTaleDto.Input) || String.IsNullOrEmpty(fairyTaleDto.Name))
            {
                return BadRequest();
            }
            FairyTale fairyTale;
            try
            {
                fairyTale = await _repository.GetFairyTaleByUserInput(fairyTaleDto);
            }
            catch (MethodAccessException e)
            {
                return StatusCode(403);
            }
            var responseFt = FairyTaleDtoGetter.GetResponseDto(fairyTale);
            return Created($"/api/FairyTaleByUserInput/{responseFt.Id}", responseFt);
        }

        /// <summary>
        /// This method can change fairy tale name by ID.
        /// </summary>
        /// <remarks>
        /// Sample request:
        ///
        ///     PATCH /api/FairyTale
        ///     {
        ///         "id": "02FCED3D-D6A1-4380-4B1C-08D91161A701",
        ///         "name": "New fairy tale"
        ///     }
        ///
        /// </remarks>
        /// <param name="ftPatchDto">DTO object that contains Ft ID and new Ft name.</param>
        /// <returns>Status code 200 if its ok.</returns>
        [HttpPatch]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult> Patch([FromBody] FairyTalePatchDto ftPatchDto)
        {
            await _repository.RenameFairyTale(ftPatchDto);

            return Ok();
        }

        /// <summary>
        /// This POST method can add fairy tale data into Store.
        /// </summary>
        /// <remarks>
        /// Sample request:
        ///
        ///     POST /api/FairyTale/AddToStore/02FCED3D-D6A1-4380-4B1C-08D91161A701
        ///
        /// </remarks>
        /// <response code="400">Returns when id less than 1</response>
        [HttpPost("AddToStore/{id:Guid}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult> AddTaleToStore(Guid id)
        {
            try
            {
                await _repository.AddTaleToStore(id);
            }
            catch (KeyNotFoundException)
            {
                return BadRequest();
            }

            return Ok();
        }

        /// <summary>
        /// This GET method returns fairy tales data by number of page and number of tales on the page from repository.
        /// </summary>
        /// <returns>Result code and an object.</returns>
        /// <remarks>
        /// Sample request:
        ///
        ///     GET /api/FairyTale/GetTalesFromStore/
        ///
        /// </remarks>
        /// <response code="200">Success</response>
        [HttpGet("GetTalesFromStore/")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public async Task<ActionResult> GetTalesFromStore([FromQuery] int startIndex, int quantity)
        {
            IEnumerable<FairyTaleResponse> fairyTales = (await _repository.GetTalesFromStore(startIndex, quantity))
                .Select(FairyTaleDtoGetter.GetResponseDto)
                .ToList();
            return Ok(fairyTales);
        }
    }
}