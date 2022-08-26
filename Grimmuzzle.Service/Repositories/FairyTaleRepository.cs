using Grimmuzzle.Service.Converters;
using Grimmuzzle.Service.DTOs;
using Grimmuzzle.Service.Interfaces;
using Grimmuzzle.Service.Models;
using Microsoft.EntityFrameworkCore;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Grimmuzzle.Service.Repositories
{
    /// <summary>
    /// Repository that provides data between API and DBContext.
    /// </summary>
    public class FairyTaleRepository : IAsyncFairyTaleRepository
    {
        private readonly GrimmuzzleContext _context;
        private readonly IFairyTaleGeneratorService _service;

        /// <summary>
        /// Create repository object.
        /// </summary>
        /// <param name="context"></param>
        /// <param name="service"></param>
        public FairyTaleRepository(GrimmuzzleContext context, IFairyTaleGeneratorService service)
        {
            _context = context;
            _service = service;
        }

        /// <summary>
        /// Get async all fairy tales from <see cref="GrimmuzzleContext"/>.
        /// </summary>
        /// <returns>List of fairy tales.</returns>
        public Task<List<FairyTale>> GetAll()
        {
            return _context.FairyTales
                    .Select(ft => ft)
                    .ToListAsync();
        }

        /// <summary>
        /// Get fairy tale by ID from <see cref="GrimmuzzleContext"/>.
        /// </summary>
        /// <param name="id">Fairy tale ID.</param>
        /// <returns>Fairy tale with given ID or <c>null</c> if it can't.</returns>
        public Task<FairyTale> Get(Guid id)
        {
            return _context.FairyTales.FirstOrDefaultAsync(ft => ft.Id.Equals(id));
        }

        public async Task RenameFairyTale(FairyTalePatchDto ftPatchDto)
        {
            var fairyTale = new FairyTale
            {
                Id = ftPatchDto.Id,
                Name = ftPatchDto.Name
            };

            _context.FairyTales.Attach(fairyTale).Property(x => x.Name).IsModified = true;
            await _context.SaveChangesAsync();
        }

        /// <summary>
        /// Create fairy tale object in <see cref="GrimmuzzleContext"/> with given parameters.
        /// </summary>
        /// <param name="fairyTaleDto">Fairy tale object DTO representation</param>
        /// <returns></returns>
        public async Task<FairyTale> Create(FairyTaleDto fairyTaleDto)
        {
            var generatedInputString = await AttributesConverter
                .Instance.GetStringRepresentation(fairyTaleDto.Input);
            try
            {
                string text = await _service.GetFairyTalesAsync(generatedInputString, fairyTaleDto); Serilog.Log.Logger.Information("Got text " + text);

                string name = GenerateName(fairyTaleDto);

                Serilog.Log.Logger.Information("Generated name " + name);

                var fairyTale = new FairyTale
                {
                    Input = generatedInputString,
                    CreationDate = DateTime.Now,
                    Length = fairyTaleDto.Length,
                    Name = name,
                    Text = text,
                    InPull = true,
                    SerializedParameters = JsonConvert.SerializeObject(fairyTaleDto.Input)
                };

                await _context.FairyTales.AddAsync(fairyTale);
                await _context.SaveChangesAsync();

                Serilog.Log.Logger.Information("Saved fairy tale with id = " + fairyTale.Id);

                return fairyTale;
            }
            catch (Exception e)
            {
                Serilog.Log.Logger.Error(e.Message);
                return null;
            }            
        }

        public string GenerateName(FairyTaleDto dto)
        {
            StringBuilder whoWhere = new StringBuilder();

            List<int> whoList = dto.Input.Who;

            for (int i = 0; i < whoList.Count; i++)
                whoWhere
                    .Append(AttributesConverter.Instance.GetParamsFromConstructor("Who", whoList[i]).Label)
                    .Append(" ");

            List<int> whereList = dto.Input.Where;

            for (int i = 0; i < whereList.Count; i++)
                whoWhere
                    .Append("in the ")
                    .Append(AttributesConverter.Instance.GetParamsFromConstructor("Where", whereList[i]).Label)
                    .Append(" ");

            //Such implementation slows down name generation. For the 'count' we can use the other number (not 'id')
            var count = _context.FairyTales.Where(ft => ft.Name.StartsWith(whoWhere.ToString())).Count();

            return whoWhere.Append(count + 1).ToString();
        }

        public async Task<FairyTale> GetFromPull(FairyTaleDto fairyTaleDto)  
        {
            _service.SendNewRequest(fairyTaleDto).ConfigureAwait(false);
            var fairyTale =  await _context.FairyTales.FirstOrDefaultAsync(ft => ft.InPull.Equals(true) &&
                ft.SerializedParameters.Equals(JsonConvert.SerializeObject(fairyTaleDto.Input)));
            if (fairyTale != null)
            {
                fairyTale.InPull = false;
                await Update(fairyTale);
                return fairyTale;
            }
            else 
            {
                fairyTale = _context.FairyTales
                    .Where(ft => ft.SerializedParameters.Equals(JsonConvert.SerializeObject(fairyTaleDto.Input)))
                    .OrderBy(x => Guid.NewGuid())
                    .FirstOrDefault();
                if (fairyTale == null) return null;
                return await CreateAndSaveDuplicate(fairyTale, fairyTaleDto);
            }
        }

        private async Task<FairyTale> CreateAndSaveDuplicate(FairyTale fairyTale, FairyTaleDto fairyTaleDto)
        {
            FairyTale duplicateFairyTale = new FairyTale()
            {
                Id = Guid.NewGuid(),
                Name = GenerateName(fairyTaleDto),
                Input = fairyTale.Input,
                Length = fairyTale.Length,
                Text = fairyTale.Text,
                SerializedParameters = fairyTale.SerializedParameters,
                InPull = false,
                CreationDate = DateTime.Now
            };
            await Save(duplicateFairyTale);
            return duplicateFairyTale;
        }

        public async Task<FairyTale> GetFairyTaleByUserInput(FairyTaleWithUserInputDto dto)
        {
            string fairyTaleText = await _service.GetFairyTaleByUserInput(dto);
            FairyTale fairyTale = new FairyTale()
            {
                Id = Guid.NewGuid(),
                Name = dto.Name,
                Input = dto.Input,
                Text = fairyTaleText,
                Length = dto.Length,
                InPull = false,
                CreationDate = DateTime.Now,
                SerializedParameters = "{}"
            };
            await Save(fairyTale);
            return fairyTale;
        }

        /// <summary>
        /// Return fairy tale object in <see cref="GrimmuzzleContext"/> with given parameters.
        /// </summary>
        /// <param name="startIndex">Number of page of fairytales</param>
        /// <param name="quantity">Length of list of fairytales</param>
        /// <returns></returns>
        public Task<List<FairyTale>> GetTalesFromStore(int startIndex, int quantity)
        {
            return _context.FairyTales
                .Where(ft => ft.InStoreDate != null)
                .OrderByDescending(ft => ft.InStoreDate)
                .Skip(startIndex)
                .Take(quantity)
                .ToListAsync();
        }

        public async Task AddTaleToStore(Guid id)
        {
            var fairyTale = new FairyTale
            {
                Id = id,
                InStoreDate = DateTime.Now
            };
            _context.FairyTales.Attach(fairyTale).Property(x => x.InStoreDate).IsModified = true;
            await _context.SaveChangesAsync();
        }
        public async Task Update(FairyTale fairyTale)
        {
            _context.Update(fairyTale);
            await _context.SaveChangesAsync();
        }

        public async Task Save(FairyTale fairyTale)
        {
            await _context.FairyTales.AddAsync(fairyTale);
            await _context.SaveChangesAsync();
        }
    }
}