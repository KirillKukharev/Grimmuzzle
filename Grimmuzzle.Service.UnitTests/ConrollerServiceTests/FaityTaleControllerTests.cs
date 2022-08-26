using System;
using System.Collections.Generic;
using NUnit.Framework;
using Grimmuzzle.Service.Models;
using Grimmuzzle.Service.Controllers;
using Grimmuzzle.Service.Interfaces;
using Moq;
using System.Threading.Tasks;
using Grimmuzzle.Service.DTOs;
using Microsoft.AspNetCore.Mvc;

namespace ServiceTesting.RepositoryServiceTests
{
    [TestFixture]
    class FaityTaleControllerTests
    {
        private Guid id = new Guid();
        [Test]

        public async Task FairyTailNotFound()
        {
            //arrange
            var repositoryMock = new Mock<IAsyncFairyTaleRepository>();
            FairyTale nullFairyTale = null;
            repositoryMock.Setup(r => r.Get(this.id)).Returns(Task.FromResult(nullFairyTale));

            FairyTaleController controller = new FairyTaleController(repositoryMock.Object);

            //act
            var result = await controller.Get(this.id);

            //assert
            Assert.NotNull(result);
        }

        [Test]
        public async Task FairyTailFound()
        {
            //arrange
            var repositoryMock = new Mock<IAsyncFairyTaleRepository>();
            FairyTale entity = new FairyTale()
            {
                Id = id,
                Input = "input",
                Name = "testName",
                Length = 256,
                InPull = false,
                CreationDate = DateTime.Now,
                SerializedParameters = "{ }"
            };
            repositoryMock.Setup(r => r.Get(this.id)).ReturnsAsync(entity);

            FairyTaleController controller = new FairyTaleController(repositoryMock.Object);

            //act
            var result = await controller.Get(this.id);

            //assert
            Assert.IsInstanceOf(typeof(OkObjectResult), result);
            var okResult = result as OkObjectResult;
            Assert.NotNull(okResult.Value);
            var dto = okResult.Value as FairyTaleResponse;
            Assert.AreEqual(entity.Length, dto.Length);
            Assert.AreEqual(entity.Name, dto.Name);
        }

        [Test]
        public async Task PostExistentFairyTaleFromPull() 
        {
            //arrange
            var repositoryMock = new Mock<IAsyncFairyTaleRepository>();
            var name = "name";
            var input = "input";
            var length = 100;
            var text = "text";
            FairyTale entity = new FairyTale()
            {
                Id = id,
                Input = input,
                Name = name,
                Text = text,
                Length = length,
                InPull = false,
                CreationDate = DateTime.Now,
                SerializedParameters = "{ }"
            };
            var dtoEntity = new FairyTaleDto
            {
                Input = new AttributesDto
                {
                    What = new List<int> {1},
                    When = new List<int> {1},
                    Where = new List<int> {1},
                    Who = new List<int> {1}
                },
                Length = 100,
                Name = "Name"
            };
            repositoryMock.Setup(r => r.GetFromPull(dtoEntity)).ReturnsAsync(entity);

            FairyTaleController controller = new FairyTaleController(repositoryMock.Object);

            //act
            var result = await controller.Post(dtoEntity);

            //assert
            Assert.NotNull(result);
            Assert.IsInstanceOf(typeof(CreatedResult), result);
            var createdResult = result as CreatedResult;
            Assert.NotNull(createdResult.Value);
            var dto = createdResult.Value as FairyTaleResponse;
            Assert.AreEqual(name, dto.Name);
            Assert.AreEqual(input, dto.Input.GeneratedString);
            Assert.AreEqual(length, dto.Length);
        }

        [Test]
        public async Task PostNonExistentFairyTaleFromPull()
        {
            //arrange
            var repositoryMock = new Mock<IAsyncFairyTaleRepository>();
            var dtoEntity = new FairyTaleDto
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
            repositoryMock.Setup(r => r.GetFromPull(dtoEntity));

            FairyTaleController controller = new FairyTaleController(repositoryMock.Object);

            //act
            var result = await controller.Post(dtoEntity);

            //assert
            repositoryMock.Verify(m => m.GetFromPull(dtoEntity), Times.Once());
        }

        private Guid id1 = Guid.NewGuid();
        private Guid id2 = Guid.NewGuid();
        private Guid id3 = Guid.NewGuid();
        private Guid id4 = Guid.NewGuid();
        private Guid id5 = Guid.NewGuid();

        [Test]
        public async Task GetTalesFromStore()
        {
            var repositoryMock = new Mock<IAsyncFairyTaleRepository>();
            List<FairyTale> TalesList = new List<FairyTale>();
            var name = "name";
            var input = "input";
            var length = 100;
            var text = "text";
            FairyTale entity = new FairyTale()
            {
                Id = id1,
                Input = input,
                Name = name,
                Text = text,
                Length = length,
                InPull = false,
                CreationDate = DateTime.Now.AddDays(2),
                InStoreDate = DateTime.Now,
                SerializedParameters = "{ }"
            };

            FairyTale entity2 = new FairyTale()
            {
                Id = id2,
                Input = input,
                Name = name,
                Text = text,
                Length = length,
                InPull = false,
                CreationDate = DateTime.Now.AddDays(3),
                InStoreDate = DateTime.Now,
                SerializedParameters = "{ }"
            };

            FairyTale entity3 = new FairyTale()
            {
                Id = id3,
                Input = input,
                Name = name,
                Text = text,
                Length = length,
                InPull = false,
                CreationDate = DateTime.Now,
                InStoreDate = DateTime.Now,
                SerializedParameters = "{ }"
            };

            FairyTale entity4 = new FairyTale()
            {
                Id = id4,
                Input = input,
                Name = name,
                Text = text,
                Length = length,
                InPull = false,
                CreationDate = DateTime.Now.AddDays(1),
                InStoreDate = DateTime.Now,
                SerializedParameters = "{ }"
            };

            FairyTale entity5 = new FairyTale()
            {
                Id = id5,
                Input = input,
                Name = name,
                Text = text,
                Length = length,
                InPull = false,
                CreationDate = DateTime.Now.AddDays(6),
                InStoreDate = DateTime.Now,
                SerializedParameters = "{ }"
            };

            TalesList.Add(entity5);
            TalesList.Add(entity2);
            TalesList.Add(entity);
            TalesList.Add(entity4);
            TalesList.Add(entity3);


            repositoryMock.Setup(r => r.GetTalesFromStore(1, 3)).ReturnsAsync(TalesList);

            FairyTaleController controller = new FairyTaleController(repositoryMock.Object);

            var result = await controller.GetTalesFromStore(1,3);

            Assert.IsInstanceOf(typeof(OkObjectResult), result);
            Assert.NotNull(result);
            var OkResult = result as OkObjectResult;
            var dto = OkResult.Value as List<FairyTaleResponse>;
            Assert.AreEqual(dto[0].Id, TalesList[0].Id);
            Assert.AreEqual(dto[1].Id, TalesList[1].Id);
            Assert.AreEqual(dto[2].Id, TalesList[2].Id);
        }
    }


}
