using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace Grimmuzzle.Service.DTOs
{
    public class AttributesDto
    {
        [JsonPropertyName(nameof(Who))]
        public List<int> Who { get; set; }

        [JsonPropertyName(nameof(What))]
        public List<int> What { get; set; }

        [JsonPropertyName(nameof(When))]
        public List<int> When { get; set; }

        [JsonPropertyName(nameof(Where))]
        public List<int> Where { get; set; }
    }
}