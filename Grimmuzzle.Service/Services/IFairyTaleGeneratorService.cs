using Grimmuzzle.Service.DTOs;
using System.Threading.Tasks;

public interface IFairyTaleGeneratorService
{
    Task<string> GetFairyTalesAsync(string input, FairyTaleDto dto);
    Task SendNewRequest(FairyTaleDto dto);
    Task<string> GetFairyTaleByUserInput(FairyTaleWithUserInputDto dto);
}
