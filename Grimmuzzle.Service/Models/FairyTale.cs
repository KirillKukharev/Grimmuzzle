using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Grimmuzzle.Service.Models
{
    /// <summary>
    /// Class representing the fairytale object model.
    /// </summary>
    public class FairyTale
    {
        public FairyTale() { }

        /// <summary>
        /// Identification number of current object.
        /// </summary>
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        [Key]
        public Guid Id { get; set; }

        /// <summary>
        /// The string retrieved from the user request.
        /// </summary>
        public string? Input { get; set; } 

        public string? SerializedParameters { get; set; }

        /// <summary>
        /// Name of current fairy tale.
        /// </summary>
        public string? Name { get; set; }

        /// <summary>
        /// Generated fairy tale text.
        /// </summary>
        public string Text { get; set; }

        /// <summary>
        /// Count of words in <c>Input</c>.
        /// </summary>
        public int Length { get; set; } 

        /// <summary>
        /// Date when object was created.
        /// </summary>
        public DateTime CreationDate { get; set; }

        /// <summary>
        /// Is the fairy tale in pull
        /// </summary>
        public bool InPull { get; set; }

        /// <summary>
        /// Date when the object added to Store
        /// </summary>
        public Nullable<DateTime> InStoreDate { get; set; }
    }
}
