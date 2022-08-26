using Grimmuzzle.Service.DTOs;
using Grimmuzzle.Service.Queue;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using System;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace Grimmuzzle.Service
{
    /// <summary>
    /// todo
    /// </summary>
    public class FairyTaleGeneratorWebRequest : IFairyTaleGeneratorService
    {
        private readonly IHttpClientFactory _clientFactory;
        private readonly IRequestsQueue _queue;
        private readonly FairyTaleGeneratorConfiguration _configurationOptions;

        public FairyTaleGeneratorWebRequest(IHttpClientFactory clientFactory, IRequestsQueue queue,
                                            IOptions<FairyTaleGeneratorConfiguration> configurationOptions)
        {
            _clientFactory = clientFactory;
            _configurationOptions = configurationOptions.Value;
            _queue = queue;
        }

        /// <summary>
        /// todo
        /// </summary>
        public async Task<string> GetFairyTalesAsync(string input, FairyTaleDto dto)
        {
            if (!_configurationOptions.useExternalMLRequest)
                return
                #region mocked log text
                    "On the other hand, we denounce with righteous " +
                    "indignation and dislike men who are so beguiled and demoralized by the " +
                    "charms of pleasure of the moment, so blinded by desire, that they cannot " +
                    "foresee the pain and trouble that are bound to ensue; and equal " +
                    "blame belongs to those who fail in their duty through weakness of " +
                    "will, which is the same as saying through shrinking from toil and pain. " +
                    "These cases are perfectly simple and easy to distinguish. In a free hour, " +
                    "when our power of choice is untrammelled and when nothing prevents our " +
                    "being able to do what we like best, every pleasure is to be welcomed " +
                    "and every pain avoided. But in certain circumstances and owing to the " +
                    "claims of duty or the obligations of business it will frequently occur that " +
                    "pleasures have to be repudiated and annoyances accepted. The wise man therefore " +
                    "always holds in these matters to this principle of selection: he rejects " +
                    "pleasures to secure other greater pleasures, or else he endures pains to avoid " +
                    "worse pains.";
            #endregion

            var client = _clientFactory.CreateClient("ML");
            Serilog.Log.Logger.Information("Send input = " + input);
            try
            {
                using var response = await client.PostAsJsonAsync("", new { input = input, length = _configurationOptions.LengthOfFairyTales });
                response.EnsureSuccessStatusCode();
                return await response.Content.ReadAsStringAsync();
            }
            catch (Exception e)
            {
                Serilog.Log.Logger.Error(e.Message);
                await SendNewRequest(dto);
            }
            throw new Exception("No response after ML-request");
        }

        public async Task<string> GetFairyTaleByUserInput(FairyTaleWithUserInputDto dto)
        {
            if (!_configurationOptions.AllowDirectRequests) throw new MethodAccessException();

            HttpClient client;
            if (Regex.Match(dto.Input, "[а-яА-ЯёЁ]").Value.Length > 0)
            {
                client = _clientFactory.CreateClient("MLRU");
            }
            else
            {
                client = _clientFactory.CreateClient("ML");
            }
            using var response = await client.PostAsJsonAsync("", new { input = dto.Input, length = _configurationOptions.LengthOfFairyTales });
            response.EnsureSuccessStatusCode();

            return await response.Content.ReadAsStringAsync();
        }

        public async Task SendNewRequest(FairyTaleDto dto)
        {
            try
            {
                await _queue.SendInputParametersToQueue(dto);
            }
            catch (Exception e) 
            {
                Console.WriteLine(e.Message);
            }
        }
    }
}