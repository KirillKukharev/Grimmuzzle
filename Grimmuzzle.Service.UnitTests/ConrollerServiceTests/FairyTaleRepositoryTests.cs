using NUnit.Framework;
using Grimmuzzle.Service.Models;
using Grimmuzzle.Service.Repositories;
using Moq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using Grimmuzzle.Service;
using Grimmuzzle.Service.DTOs;
using Microsoft.Extensions.Configuration;
using System;
using System.Threading;
using System.Linq.Expressions;
using Microsoft.EntityFrameworkCore.Query;

namespace ServiceTesting.RepositoryServiceTests
{
    [TestFixture]
    class FaityTaleRepositoryTests
    {
        private System.Guid id = new System.Guid();
        private Guid id2 = Guid.NewGuid();
        private Guid id3 = Guid.NewGuid();
        private Guid id4 = Guid.NewGuid();
        private Guid id5 = Guid.NewGuid();
        private Guid id6 = Guid.NewGuid();
        private Guid id7 = Guid.NewGuid();
        private Guid id8 = Guid.NewGuid();
        private Guid id9 = Guid.NewGuid();
        private Guid id10 = Guid.NewGuid();
        private Guid id11 = Guid.NewGuid();
        private Guid id12 = Guid.NewGuid();

        
        [Test]
        [Ignore("Ignore a test")]
        public async Task GetNonExistentFairyTaleFromPullTest()
        {
            //arrange
            #region Configuration settings

            AppUtils.Configuration = new ConfigurationBuilder()
                .AddJsonStream(new MemoryStream(Encoding.Default.GetBytes(MemorySetting())))
                .Build();

            #endregion

            var mockContext = new Mock<GrimmuzzleContext>();
            mockContext.Setup(c => c.FairyTales).Returns(MockSetConfigs().Object);


            var serviceMock = new Mock<IFairyTaleGeneratorService>();
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

            serviceMock.Setup(s => s.SendNewRequest(dtoEntity));
            FairyTaleRepository repository = new FairyTaleRepository(mockContext.Object, serviceMock.Object);

            //act
            var fairyTale = await repository.GetFromPull(dtoEntity);

            //assert
            serviceMock.Verify(m => m.SendNewRequest(dtoEntity), Times.Once);
        }

        [Test]
        public async Task ShouldBeReturnConstantListIfQuantityOfEnteredTalesIsGreaterThanExistInDataTest()
        {
            //arrange
            #region Configuration settings

            AppUtils.Configuration = new ConfigurationBuilder()
                .AddJsonStream(new MemoryStream(Encoding.Default.GetBytes(MemorySetting())))
                .Build();

            #endregion

            var mockContext = new Mock<GrimmuzzleContext>();
            mockContext.Setup(c => c.FairyTales).Returns(MockSetConfigs().Object);

            var serviceMock = new Mock<IFairyTaleGeneratorService>();

            FairyTaleRepository repository = new FairyTaleRepository(mockContext.Object, serviceMock.Object);

            //act
            var fairyTalesSize2 = await repository.GetTalesFromStore(1, 3);
            var fairyTalesSize3 = await repository.GetTalesFromStore(1, 10000);


            //assert
            Assert.NotNull(fairyTalesSize2);
            Assert.AreEqual(fairyTalesSize2.Count, 3);

            Assert.NotNull(fairyTalesSize3);
            Assert.AreEqual(fairyTalesSize3.Count, 7);
            Assert.AreEqual(fairyTalesSize3.ToList()[0].Id, fairyTalesSize2.ToList()[0].Id);
            Assert.AreEqual(fairyTalesSize3.ToList()[1].Id, fairyTalesSize2.ToList()[1].Id);
            Assert.AreEqual(fairyTalesSize3.ToList()[2].Id, fairyTalesSize2.ToList()[2].Id);
        }

        [Test]
        public async Task TalesFromStoreShouldBeSortedByDate()
        {
            //arrange
            #region Configuration settings

            AppUtils.Configuration = new ConfigurationBuilder()
                .AddJsonStream(new MemoryStream(Encoding.Default.GetBytes(MemorySetting())))
                .Build();

            #endregion

            var mockContext = new Mock<GrimmuzzleContext>();
            mockContext.Setup(c => c.FairyTales).Returns(MockSetConfigs().Object);

            var serviceMock = new Mock<IFairyTaleGeneratorService>();

            FairyTaleRepository repository = new FairyTaleRepository(mockContext.Object, serviceMock.Object);

            //act
            var fairyTales = await repository.GetTalesFromStore(1, 4);

            //assert
            Assert.AreEqual(fairyTales.Count, 4);
            for (int i = 1; i < 4; i++)
            {
                Assert.IsTrue(fairyTales.ToList()[i].InStoreDate < fairyTales.ToList()[i - 1].InStoreDate);
            }
        }


        [Test]
        public async Task ReturnCorrectValuesInResponseToVariousStartIndex()
        {
            //arrange

            #region Configuration settings

            AppUtils.Configuration = new ConfigurationBuilder()
                .AddJsonStream(new MemoryStream(Encoding.Default.GetBytes(MemorySetting())))
                .Build();

            #endregion

            var mockContext = new Mock<GrimmuzzleContext>();
            mockContext.Setup(c => c.FairyTales).Returns(MockSetConfigs().Object);

            var serviceMock = new Mock<IFairyTaleGeneratorService>();

            FairyTaleRepository repository = new FairyTaleRepository(mockContext.Object, serviceMock.Object);

            //act
            var fairyTalesSize2 = await repository.GetTalesFromStore(4, 2);
            var fairyTalesSize3 = await repository.GetTalesFromStore(3, 3);
            var fairyTalesSize4 = await repository.GetTalesFromStore(2, 4);
            var sortedTalesByDate = LoadFakeData().OrderByDescending(ft => ft.InStoreDate);


            //assert
            Assert.AreEqual(fairyTalesSize2.Count, 2);
            Assert.AreEqual(fairyTalesSize3.Count, 3);
            Assert.AreEqual(fairyTalesSize4.Count, 4);

            for (int i = 0; i < fairyTalesSize2.Count; i++)
            {
                Assert.AreEqual(fairyTalesSize2.ToList()[i].Id, sortedTalesByDate.ToList()[i + 4].Id);
            }
            
            for (int i = 0; i < fairyTalesSize3.Count; i++)
            {
                Assert.AreEqual(fairyTalesSize3.ToList()[i].Id, sortedTalesByDate.ToList()[i + 3].Id);
            }
            
            for (int i = 0; i < fairyTalesSize4.Count; i++)
            {
                Assert.AreEqual(fairyTalesSize4.ToList()[i].Id, sortedTalesByDate.ToList()[i + 2].Id);
            }

        }

        private IQueryable<FairyTale> LoadFakeData()
        {
            var data = new List<FairyTale>
            {
                new FairyTale { Id = this.id,  Name = "A", Input = "AA", Text = "AAA", Length = 100, InStoreDate = null, CreationDate = DateTime.Now.AddDays(40) },
                new FairyTale { Id = this.id10,Name = "B", Input = "BB", Text = "BBB", Length = 100, InStoreDate = DateTime.Now.AddMinutes(10), CreationDate = DateTime.Now.AddDays(12) },
                new FairyTale { Id = this.id11,Name = "D", Input = "DD", Text = "DDD", Length = 100, InStoreDate = DateTime.Now.AddMinutes(20), CreationDate = DateTime.Now.AddDays(22) },
                new FairyTale { Id = this.id2, Name = "O", Input = "OO", Text = "OOO", Length = 200, InStoreDate = null, CreationDate = DateTime.Now.AddDays(1) },
                new FairyTale { Id = this.id3, Name = "E", Input = "EE", Text = "EEE", Length = 300, InStoreDate = DateTime.Now.AddMinutes(5), CreationDate = DateTime.Now },
                new FairyTale { Id = this.id4, Name = "C", Input = "CC", Text = "CCC", Length = 400, InStoreDate = null, CreationDate = DateTime.Now.AddDays(2) },
                new FairyTale { Id = this.id5, Name = "M", Input = "MM", Text = "MMM", Length = 100, InStoreDate = DateTime.Now.AddMinutes(15), CreationDate = DateTime.Now.AddDays(3) },
                new FairyTale { Id = this.id6, Name = "K", Input = "KK", Text = "KKK", Length = 200, InStoreDate = DateTime.Now, CreationDate = DateTime.Now.AddDays(4) },
                new FairyTale { Id = this.id7, Name = "L", Input = "LL", Text = "LLL", Length = 200, InStoreDate = DateTime.Now.AddMinutes(30), CreationDate = DateTime.Now.AddDays(8) },
                new FairyTale { Id = this.id8, Name = "N", Input = "NN", Text = "NNN", Length = 200, InStoreDate = DateTime.Now.AddMinutes(40), CreationDate = DateTime.Now.AddDays(5) },
                new FairyTale { Id = this.id9, Name = "G", Input = "GG", Text = "GGG", Length = 200, InStoreDate = DateTime.Now.AddMinutes(35), CreationDate = DateTime.Now.AddDays(6) }
            }.AsAsyncQueryable();
            return data;
        }

        public string MemorySetting()
        {
            var inMemorySettings = @"
            {
              ""Who"": {
                        ""multiple"": true,
                        ""items"": [
                        {
                            ""id"": 1,
                            ""img_url"": ""https://sqlvaaq7beekpgingw.blob.core.windows.net/who/_boy.png"",
                            ""label"": ""Boy"",
                            ""text"": ""smart boy""
                        }
                        ]
            },
            ""What"": {
                        ""multiple"": true,
                        ""items"": [
                        {
                            ""id"": 1,
                            ""img_url"": ""https://sqlvaaq7beekpgingw.blob.core.windows.net/who/_boy.png"",
                            ""label"": ""Burn"",
                            ""text"": ""burned""
                        }
                        ]
            },
            ""Where"": {
                        ""multiple"": true,
                        ""items"": [
                        {
                            ""id"": 1,
                            ""img_url"": ""https://sqlvaaq7beekpgingw.blob.core.windows.net/who/_boy.png"",
                            ""label"": ""School"",
                            ""text"": ""at school""
                        }
                        ]
            },
            ""When"": {
                        ""multiple"": true,
                        ""items"": [
                        {
                            ""id"": 1,
                            ""img_url"": ""https://sqlvaaq7beekpgingw.blob.core.windows.net/who/_boy.png"",
                            ""label"": ""Idk"",
                            ""text"": ""i dont know""
                        }
                        ]
            }
            }";
            return inMemorySettings;
        }

        public Mock<DbSet<FairyTale>> MockSetConfigs()
        {
            var mockSet = new Mock<DbSet<FairyTale>>();
            mockSet.As<IQueryable<FairyTale>>().Setup(m => m.Provider).Returns(LoadFakeData().Provider);
            mockSet.As<IQueryable<FairyTale>>().Setup(m => m.Expression).Returns(LoadFakeData().Expression);
            mockSet.As<IQueryable<FairyTale>>().Setup(m => m.ElementType).Returns(LoadFakeData().ElementType);
            mockSet.As<IQueryable<FairyTale>>().Setup(m => m.GetEnumerator()).Returns(LoadFakeData().GetEnumerator());
            return mockSet;
        }
    }


    public static class AsyncQueryable
    {
        /// <summary>
        /// Returns the input typed as IQueryable that can be queried asynchronously
        /// </summary>
        /// <typeparam name="TEntity">The item type</typeparam>
        /// <param name="source">The input</param>
        public static IQueryable<TEntity> AsAsyncQueryable<TEntity>(this IEnumerable<TEntity> source)
            => new AsyncQueryableExtension<TEntity>(source ?? throw new ArgumentNullException(nameof(source)));
    }

    
    public class AsyncQueryableExtension<TEntity> : EnumerableQuery<TEntity>, IAsyncEnumerable<TEntity>, IQueryable<TEntity>
    {
        public AsyncQueryableExtension(IEnumerable<TEntity> enumerable) : base(enumerable) { }
        public AsyncQueryableExtension(Expression expression) : base(expression) { }
        public IAsyncEnumerator<TEntity> GetEnumerator() => new AsyncEnumerator(this.AsEnumerable().GetEnumerator());
        public IAsyncEnumerator<TEntity> GetAsyncEnumerator(CancellationToken cancellationToken = default) => new AsyncEnumerator(this.AsEnumerable().GetEnumerator());
        IQueryProvider IQueryable.Provider => new AsyncQueryProvider(this);

        class AsyncEnumerator : IAsyncEnumerator<TEntity>
        {
            private readonly IEnumerator<TEntity> inner;
            public AsyncEnumerator(IEnumerator<TEntity> inner) => this.inner = inner;
            public void Dispose() => inner.Dispose();
            public TEntity Current => inner.Current;
            public ValueTask<bool> MoveNextAsync() => new ValueTask<bool>(inner.MoveNext());
            #pragma warning disable CS1998 // Nothing to await
            public async ValueTask DisposeAsync() => inner.Dispose();
            #pragma warning restore CS1998
        }

        class AsyncQueryProvider : IAsyncQueryProvider
        {
            private readonly IQueryProvider inner;
            internal AsyncQueryProvider(IQueryProvider inner) => this.inner = inner;
            public IQueryable CreateQuery(Expression expression) => new AsyncQueryableExtension<TEntity>(expression);
            public IQueryable<TElement> CreateQuery<TElement>(Expression expression) => new AsyncQueryableExtension<TElement>(expression);
            public object Execute(Expression expression) => inner.Execute(expression);
            public TResult Execute<TResult>(Expression expression) => inner.Execute<TResult>(expression);
            public IAsyncEnumerable<TResult> ExecuteAsync<TResult>(Expression expression) => new AsyncQueryableExtension<TResult>(expression);
            TResult IAsyncQueryProvider.ExecuteAsync<TResult>(Expression expression, CancellationToken cancellationToken) => Execute<TResult>(expression);
        }

    }
}
