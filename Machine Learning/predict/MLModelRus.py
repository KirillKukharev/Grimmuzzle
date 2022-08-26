from transformers import GPT2LMHeadModel, GPT2Tokenizer

tok = GPT2Tokenizer.from_pretrained("model")  # initialize tokenizer from folder
model = GPT2LMHeadModel.from_pretrained("model")  # initialize model from folder


def generation_text(data_string, string_length):
    """
    Produce text from input parameters
    :param data_string: string of text, as an input sequence for model
    :param string_length: integer value to configure length of generated sequence
    :return: sequence of generated text, where words after the last punctuation mark have been removed
    """

    inpt = tok.encode(data_string, return_tensors="pt")
    out = model.generate(inpt, max_length=string_length + 100, repetition_penalty=1.4, top_k=50,
                         top_p=0.95,
                         temperature=0.7, do_sample=True, num_return_sequences=1, bad_words_ids=[[203]])
    result = tok.decode(out[0])
    if result.find('\n') != -1:
        text = result.split('\n')[0]
    else:
        text = result

    for i in reversed(range(len(text))):
        if text[i] in '!.?;':
            text = text[: i + 1]
            break
    return text
