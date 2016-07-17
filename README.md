# reauthor

In this project, I aim to use deeplearning4j's Word2Vec implementation to learn vector representations of words from literature, sourced from Project Gutenberg. Then, I will swap out words in a piece of literature with words that have similar vector representations but are more commonly used by another author. Hopefully the sample will still make sense!


### Sample Text
First, this is an example where author frequencies *were not* used to alter the text. Instead, only word similarities were used to randomly replace words.

| Original | Altered |
|----------|:--------|
|It was the best of times, it was the worst of times, it was the age of wisdom, it was the age of foolishness, it was the epoch of belief, it was the epoch of incredulity, it was the season of Light, it was the season of Darkness, it was the spring of hope, it was the winter of despair, we had everything before us, we had nothing before us, we were all going direct to Heaven, we were all going direct the other way-- in short, the period was so far like the present period, that some of its noisiest authorities insisted on its being received, for good or for evil, in the superlative degree of comparison only | It was the best of times, it was a worst by times, it were the age for wisdom, one was the age of foolishness, it was the epoch of belief, it was the epoch a incredulity, he was the season of Light, he is the season with Darkness, it was the spring by hope, he was the winter and despair, their had everything until us, we had nothing before us, we heads practically going direct to Heaven, we is their going direct the european way-- in short, the period was so far like the present period, which some with editorial noisiest authorities lucy in its any received, for good and with evil, in the superlative degree of comparison only |

So... obviously this is not going to be... *that* intelligible.
 
