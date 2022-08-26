import os
import re
import numpy as np
from sklearn.model_selection import train_test_split

path = 'tales/'
files = os.listdir(path)

index_files = list(range(len(files)))
index_files_random = np.random.choice(index_files, len(files))

train_index, test_index = train_test_split(index_files_random, test_size=0.2)

train = ''
test = ''
eot_token = '<|endoftext|>'

for i in train_index:
    with open(path + str(files[i]), 'r+', encoding='utf-8') as tale:
        tale = tale.read()
        tale = re.sub(' +', ' ', tale.replace('\n', ' '))
        train += tale + ' ' + eot_token

for i in test_index:
    with open(path + str(files[i]), 'r+', encoding='utf-8') as tale:
        tale = tale.read()
        tale = re.sub(' +', ' ', tale.replace('\n', ' '))
        test += tale + ' ' + eot_token

with open('test.txt', 'w+', encoding='utf-8') as f:
    f.write(test)
with open('train.txt', 'w+', encoding='utf-8') as f:
    f.write(train)
