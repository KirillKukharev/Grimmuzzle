from aitextgen import aitextgen

ai = aitextgen(model_folder="model", to_gpu=False)  # initialize model from folder

def generation_text(data_string, string_length):
    """
    Produce text from input parameters
    :param data_string: string of text, as an input sequence for model
    :param string_length: integer value to configure length of generated sequence
    :return: sequence of generated text, where words after the last punctuation mark have been removed
    """

    text = ai.generate_one(prompt=data_string,
                           max_length=string_length + 100,
                           min_length=string_length,
                           repetition_penalty=1.4,
                           top_p=0.9).replace("\n", " ").replace(";", ".")

    for i in reversed(range(len(text))):
        if text[i] in '!.?':
            text = text[: i + 1]
            break
    return text
