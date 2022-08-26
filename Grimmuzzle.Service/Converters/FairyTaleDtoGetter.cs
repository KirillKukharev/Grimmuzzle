using Grimmuzzle.Service.DTOs;
using Grimmuzzle.Service.Models;
using Newtonsoft.Json;

namespace Grimmuzzle.Service.Converters
{
    internal static class FairyTaleDtoGetter
    {
        public static FairyTaleResponse GetResponseDto(FairyTale fairyTale)
        {
            var input = new GenerationParamsDto
            {
                Attributes = JsonConvert.DeserializeObject<AttributesDto>(fairyTale.SerializedParameters),
                GeneratedString = fairyTale.Input
            };

            return new FairyTaleResponse
            {
                Id = fairyTale.Id,
                Name = fairyTale.Name,
                Text = fairyTale.Text,
                TimeStamp = fairyTale.CreationDate,
                Length = fairyTale.Length,
                Input = input,
                InStore = fairyTale.InStoreDate != null
            };
        }
    }
}