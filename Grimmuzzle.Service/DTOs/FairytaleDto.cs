using Grimmuzzle.Service.Models;
using System;
using System.Text.Json.Serialization;

namespace Grimmuzzle.Service.DTOs
{
    /// <summary>
    /// Data transfer object of <see cref="FairyTale"/>
    /// </summary>
    public class FairyTaleDto
    {
        [JsonPropertyName("input")]
        public AttributesDto Input { get; set; }

        [JsonPropertyName("length")]
        [ObsoleteAttribute("This property is obsolete. Use LengthOfFairyTales from appsettings.json.", false)]
        public int Length { get; set; }

        [JsonPropertyName("name")]
        public string Name { get; set; }
    }
}
