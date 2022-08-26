using Grimmuzzle.Service.Converters.Resources;
using Grimmuzzle.Service.DTOs;
using Microsoft.Extensions.Configuration;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Grimmuzzle.Service.Converters
{
    public class AttributesConverter : IRepresentationGetter
    {
        private static IRepresentationGetter _representationInstance;
        private static readonly Random _random;

        public static IRepresentationGetter Instance
        {
            get { return _representationInstance ??= new AttributesConverter(); }
            set => _representationInstance = value;
        }

        static AttributesConverter()
        {
            _random = new Random();
        }

        public async Task<string> GetStringRepresentation(AttributesDto attributes)
        {
            var isPlural = attributes.Who.Count > 1;
            var template = await GetRandomTaleBeginnerAsync(Path.Combine(AppContext.BaseDirectory, "taleBeginningTemplates.json"),
                isPlural);
            var inputStringBuilder = new StringBuilder(template);

            var attributeGroupNames = typeof(AttributesDto).GetProperties()
                .Select(pi => pi.Name)
                .ToList();

            var attributeLists = new Dictionary<string, List<int>>();
            foreach (var attributeGroup in attributeGroupNames)
            {
                var propValue = attributes
                    .GetType()
                    .GetProperty(attributeGroup)?
                    .GetValue(attributes, null) as List<int>;

                attributeLists.Add(attributeGroup, propValue);
            }

            foreach (var (groupName, groupContent) in attributeLists)
            {
                var parametersList = groupContent
                    .Select(id => GetParamsFromConstructor(groupName, id).Text);
                string names;
                if (groupName.Equals("Who") && isPlural)
                {
                    names = GenerateMultiplyCharactersString(parametersList.ToList());
                }
                else
                {
                    names = string.Join(", ", parametersList);
                }

                inputStringBuilder = inputStringBuilder.Replace($"{{{groupName}}}", names);
            }

            return inputStringBuilder.ToString();
        }

        private async Task<string> GetRandomTaleBeginnerAsync(string path, bool isPlural)
        {
            var serializedBeginners = await File.ReadAllTextAsync(path);
            var beginners = JsonConvert
                .DeserializeAnonymousType(serializedBeginners, new
                {
                    items = new List<string>(),
                    items_plural = new List<string>(),
                    items_singular = new List<string>()
                });

            var startList = isPlural ? beginners.items_plural : beginners.items_singular;
            startList.AddRange(beginners.items);
            var index = _random.Next(0, startList.Count);

            return startList[index];
        }

        private string GenerateMultiplyCharactersString(List<string> characters)
        {
            var sb = new StringBuilder();
            var count = characters.Count;
            for (var i = 0; i < count - 2; i++)
            {
                sb.Append(characters[i]).Append(", ");
            }
            sb.Append(characters[count - 2]).Append(" with a ").Append(characters[count - 1]);
            return sb.ToString();
        }

        public AttributeParameters GetParamsFromConstructor(string groupName, int id)
        {
            return AppUtils.Configuration
                .GetSection($"{groupName}:items")
                .Get<List<AttributeParameters>>()
                .FirstOrDefault(ap => ap.Id == id) ??
                   throw new ArgumentException(
                       "AttributesDto did not contains one of required attribute group/group is empty");
        }
    }
}