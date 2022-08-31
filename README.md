# Learning Rules from KGs Guided by Language Models
## Introduction ##
* We present a method for exploiting language models to support knowledge
graph-based rule learning process, which relies on the prompt generation to
query language models for ranking predictions made by rules. Getting a pre-
diction is considered as a link prediction task because we are predicting the
[MASK] entity in the triple ```<subject, predicate, [MASK]>```.
* To further boost the quality of the language model-based prediction ranking,
we propose a novel approach for expanding language model prompts by gen-
erating hyponyms that could be used as paraphrases of the given predicate.
* We build on the system developed by [1].
## prerequisites ##
```Python ``` (v3.9.12), ```Torch ``` (v1.11.0), ```Torch ``` (v4.18.0)
```jdk``` (v1.8.0), ```maven``` (v3.8.5)
## Running Instructions ##
To run this project, first compile using:
```
mvn compile
```
Then, navigate to the api folder by typing:
```
cd api
```
Then start the server:
```
nohup python3 server.py &
```
This command starts the server. After executing this command, hit the Enter Key
again. This will allow you to execute another command at the same time. The pro-
cess started under the nohup command will not stop even if the session has been
disconnected.

To start running the system, run the following commands:
```
$ cd -
$ bash run.sh -w <workspace> -em <embedding model> -xyz -ew <λ> -nw <number of workers>
```
There are several options for -em.
* ```bert```: this will run the baseline model. The baseline model will only use a
prompt ```subject predicate [MASK].```
* ```hypercontx```: this will first use contextualised information to generate hyponyms
for the predicate in a rule. Then the predicted hyponym will be incorporated
into the prompt. An example of the prompt will be ```subject predicte or hyponym [MASK].```
* ```hyperdir```: this will directly predict hyponyms without any contextualised in-
formation. Then the predicted hyponym will be incorporated into the prompt.
An example of the prompt will be ```subject predicte or hyponym [MASK].```
* ```hyconquy```: this will first use contextualised information to generate hyponyms
for the predicate in a rule. Then, the predicate in the prompt will be replaced
by the generated hyponym.

An example of running the baseline model will be:
```
$ bash run.sh -em bert -w ./data/imdb -xyz -nw 4 -ew 1
```
The system also provides a script to directly output a list of hyponyms for each pred-
icate for inspection purposes. The list of hyponyms can be obtained by running the
following command:
```
$ bash hypernym.sh <workspace>
```
An example will be ```$ bash hypernym.sh ./data/imdb```.
In order to plot the data, the following command can be used:
```
$ python3 plot.py --w <workspace> --f <file name> --n <user-defined name for plotted graph>
```
For example, to plot a line graph for the baseline model, one can run the following:
```
$ python3 plot.py --w ./BASELINE --f BASELINE.csv --n BASELINE
```
## Reference ##
[1] V. Thinh Ho, D. Stepanova, M. Gad-Elrab, E. Kharlamov and G. Weikum. Rule Learning from Knowledge Graphs Guided by Embedding Models. In Proc. 17th International Semantic Web Conference (ISWC 2018), to appear, 2018.
