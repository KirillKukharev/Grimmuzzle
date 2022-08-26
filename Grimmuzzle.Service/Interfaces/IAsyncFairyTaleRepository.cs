using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Grimmuzzle.Service.DTOs;
using Grimmuzzle.Service.Models;

namespace Grimmuzzle.Service.Interfaces
{
    public interface IAsyncFairyTaleRepository
    {
        Task<List<FairyTale>> GetAll();
        Task<FairyTale> Get(Guid id);
        Task<FairyTale> Create(FairyTaleDto fairyTaleDto);
        Task RenameFairyTale(FairyTalePatchDto ftPatchDto);
        Task<FairyTale> GetFromPull(FairyTaleDto dto);
        string GenerateName(FairyTaleDto dto);
        Task<List<FairyTale>> GetTalesFromStore(int startIndex, int quantity);
        Task AddTaleToStore(Guid id);
        Task<FairyTale> GetFairyTaleByUserInput(FairyTaleWithUserInputDto dto);
    }
}