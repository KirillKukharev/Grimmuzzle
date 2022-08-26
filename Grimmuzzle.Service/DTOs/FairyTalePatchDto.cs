using System;
using System.Text.Json.Serialization;

namespace Grimmuzzle.Service.DTOs
{
    public class FairyTalePatchDto
    {
        [JsonPropertyName("id")]
        public Guid Id { get; set; }

        [JsonPropertyName("name")]
        public string Name { get; set; }
    }
}