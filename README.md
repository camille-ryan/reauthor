# reauthor

In this project, I aim to use deeplearning4j's Word2Vec implementation to learn vector representations of words from literature, sourced from Project Gutenberg. Then, I will swap out words in a piece of literature with words that have similar vector representations but are more commonly used by another author. Hopefully the sample will still make sense!

### Replacement Method

Let m = the number of words in the sample to reauthor, and let n = number of words in the vocabulary.

For each word in the sample, find cosine distances to all other words in the vocabulary. The range of the cosine distance is \[0, 1\]. This is a vector of size n.

For each word w<sub>i</sub> in the vocabulary, find the ratio P(W = w<sub>i</sub> | Author) / P(W = w<sub>i</sub>). This is also a vector of size n. 

Multiply the above two vectors, and normalize them so that the sum is equal to 1. This vector of size n is used as the selection probability for a replacement word.

The complexity for this replacement method ought to be O(m*n), and should take much less time than actually fitting the vector representations in the first place.
