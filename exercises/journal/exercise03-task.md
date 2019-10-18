# Exercise 3 - Adaptive Behaviour

## Setting
The setting is very similar to the one from the last exercise. There are mortal consumers with a daily death probability of 1% that work and consume in a setting in which the central bank owns all the firms but forwards the dividends to the consumers in proportion to how much money they have.

## Task
Find an adaptive method that allows the [DiscountingConsumer](../src/com/agentecon/exercise3/DiscountingConsumer.java) to endogenously find the optimal amount of money to keep as a reserve, as defined by the capitalBuffer variable (in the previous exercise, this was a constant). When running the [Configuration](../src/com/agentecon/exercise3/Configuration.java), it tells you how close your agents ended up being to the approximately optimal buffer size. Also have a look at the interest statistic when running the local server. You will find that it is not easy to find an algorithm that leads to a stable simulation (maybe not at all). This is often the case in chaotic, interdependent systems.

## Deadline

The deadline for submitting your agents and the lab journal to github is 2019-10-23 at 24:00.