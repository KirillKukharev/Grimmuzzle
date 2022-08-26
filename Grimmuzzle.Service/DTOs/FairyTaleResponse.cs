using System;
using System.Text.Json.Serialization;

namespace Grimmuzzle.Service.DTOs
{
    public class FairyTaleResponse
    {
        [JsonPropertyName("id")]
        public Guid Id { get; set; }

        [JsonPropertyName("name")]
        public string Name { get; set; }

        [JsonPropertyName("text")]
        public string Text { get; set; }

        [JsonPropertyName("timestamp")]
        public DateTime TimeStamp { get; set; }

        [JsonPropertyName("length")]
        public int Length { get; set; }

        [JsonPropertyName("instore")]
        public bool InStore { get; set; }

        [JsonPropertyName("input")]
        public GenerationParamsDto Input { get; set; }
    }
}