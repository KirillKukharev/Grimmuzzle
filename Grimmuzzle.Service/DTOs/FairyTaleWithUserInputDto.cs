using Grimmuzzle.Service.Models;
using System.Text.Json.Serialization;

namespace Grimmuzzle.Service.DTOs
{
    /// <summary>
    /// Data transfer object of <see cref="FairyTale"/>
    /// </summary>
    public class FairyTaleWithUserInputDto
    {
        [JsonPropertyName("name")]
        public string Name { get; set; }

        [JsonPropertyName("input")]
        public string Input { get; set; }

        [JsonPropertyName("length")]
        public int Length { get; set; }
    }
}
