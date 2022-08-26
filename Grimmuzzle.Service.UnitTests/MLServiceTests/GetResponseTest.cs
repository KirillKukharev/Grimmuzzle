using Grimmuzzle.Service.DTOs;
using Grimmuzzle.Service.Queue;
using Microsoft.Extensions.Options;
using Moq;
using Moq.Protected;
using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;

namespace Grimmuzzle.Service.UnitTests.MLServiceTests
{
    [TestFixture]
    public class GetResponseTest
    {
        private IFairyTaleGeneratorService _webService;
        private bool _isSendAsyncInvoked;
        private string _input;
        private int _length;
        private FairyTaleDto _dto;

        private void Init(bool useExternalMlRequest,
            HttpStatusCode code = HttpStatusCode.OK)
        {
            var responseMessage = new HttpResponseMessage(code)
            {
                Content = new StringContent("Some words in very big fairy tail")
            };

            var handlerMock = new Mock<HttpMessageHandler>();
            handlerMock
                .Protected()
                .Setup<Task<HttpResponseMessage>>("SendAsync", ItExpr.IsAny<HttpRequestMessage>(),
                    ItExpr.IsAny<CancellationToken>())
                .Callback(() => _isSendAsyncInvoked = true)
                .ReturnsAsync(responseMessage);

            var client = new HttpClient(handlerMock.Object)
            {
                BaseAddress = new Uri("http://site.cc")
            };

            #region Factory mock

            var mockFactory = new Mock<IHttpClientFactory>();
            mockFactory
                .Setup(factory => factory.CreateClient("ML"))
                .Returns(client);

            #endregion

            #region Config mock

            var configMock = new Mock<IOptions<FairyTaleGeneratorConfiguration>>();
            configMock
                .SetupGet(opt => opt.Value)
                .Returns(new FairyTaleGeneratorConfiguration { useExternalMLRequest = useExternalMlRequest });

            #endregion

            var queueMock = new Mock<IRequestsQueue>();

            _webService = new FairyTaleGeneratorWebRequest(mockFactory.Object, queueMock.Object, configMock.Object);
        }

        [SetUp]
        public void SetUp()
        {
            _input = "some words";
            _length = 2;
            _isSendAsyncInvoked = false;
            _dto = new FairyTaleDto
            {
                Input = new AttributesDto
                {
                    What = new List<int> { 1 },
                    When = new List<int> { 1 },
                    Where = new List<int> { 1 },
                    Who = new List<int> { 1 }
                },
                Length = 100,
                Name = "Name"
            };
        }

        [Test]
        public async Task GetFairyTaleStringAfterSendingParameters()
        {
            //arrange
            const string expected = "Some words in very big fairy tail";
            Init(true);

            //act
            var response = await _webService.GetFairyTalesAsync(_input, _dto);

            //assert
            Assert.AreEqual(expected, response);
            Assert.IsTrue(_isSendAsyncInvoked);
        }

        [Test]
        public async Task GetMockedStringIfUseExternalMlRequestEqualsFalse()
        {
            //arrange
            Init(false);

            //act
            await _webService.GetFairyTalesAsync(_input, _dto);

            //assert
            Assert.IsFalse(_isSendAsyncInvoked);
        }

        [Test]
        [Ignore("Ignore a test")]
        public void ThrowsAnExceptionWhenGetsCode500()
        {
            //arrange
            const HttpStatusCode code = HttpStatusCode.InternalServerError;
            Init(true, code);

            //act, assert
            Assert.ThrowsAsync<HttpRequestException>(async () =>
            {
                await _webService.GetFairyTalesAsync("any string", _dto);
            });
        }
    }
}