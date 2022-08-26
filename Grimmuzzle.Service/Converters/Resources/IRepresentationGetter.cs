using Grimmuzzle.Service.DTOs;
using System.Threading.Tasks;

namespace Grimmuzzle.Service.Converters.Resources
{
    public interface IRepresentationGetter
    {
        Task<string> GetStringRepresentation(AttributesDto attributes);
        AttributeParameters GetParamsFromConstructor(string groupName, int id);
    }
}