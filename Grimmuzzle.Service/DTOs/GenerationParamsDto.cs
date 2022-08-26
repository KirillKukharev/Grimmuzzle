using System.Text.Json.Serialization;

namespace Grimmuzzle.Service.DTOs
{
    public class GenerationParamsDto
    {
        [JsonPropertyName("attributes")]
        public AttributesDto Attributes { get; set; }

        [JsonPropertyName("generatedString")]
        public string? GeneratedString { get; set; }
    }
}