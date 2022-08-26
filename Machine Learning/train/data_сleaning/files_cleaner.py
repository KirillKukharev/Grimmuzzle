import os

def clean_space(string):
    """
    Removes spaces at the beginning of a line
    :param string:
        The line of text to check.
    :return:
        Clean line of text
    """
    string = ' '.join(string.split())
    return string

def remove_invalid_symbols(dir):
    """
    Remove invalid symbols in textfile
    :param dir:
        Directory with fairytales, which needs to be cleaned of unnecessary symbols
    :return:
        Cleaned fairytales files
    """
    for x in os.listdir(dir):  # loop through all files in a directory
        fullname = os.path.join(dir, x)
        with open(fullname, "w") as file_in:
            lines = file_in.readlines()  # read text in file
            for line in lines:
                line = clean_space(line)  # call function to remove spaces between words
                file_in.write(line)

        with open(fullname, "w") as file_out:
            text = file_out.read()
            text = text.translate(
                str.maketrans({'\n\n': ' ', '--': ' ', '-': ' ', ';': '.', 'NOTE': '', '&': ' ', '[1]': ' ', '[2]': ' ',
                               '[3]': ' ', '[4]': '', '[5]': ' ', '[6]': ' ', '[7]': ' ', '[8]': '', '[9]': ' ', '[10]': '',
                               '[': '(', ']': ')', 'Illustration': ''})
            )
            file_out.write(text)  # overwrite the file


dir = "./fairytales"  # directory with fairytales files
remove_invalid_symbols(dir)
