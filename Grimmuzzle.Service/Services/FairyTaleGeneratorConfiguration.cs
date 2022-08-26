namespace Grimmuzzle.Service
{
    public class FairyTaleGeneratorConfiguration
    {
        public bool useExternalMLRequest { get; set; }
        public int MaxRequestsCount { get; set; }
        public bool AllowDirectRequests { get; set; }
        public int LengthOfFairyTales { get; set; }
    }
}
