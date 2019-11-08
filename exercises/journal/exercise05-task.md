# Exercise 5 - Flow

## Setting
This exercise is based on the [financial economy setting without funds](http://meissereconomics.com/vis/simulation?sim=ex4-financial-economy-no-funds-1). As before, there are mortal consumers that save by buying stocks while working and later sell them in retirement. The consumers follow a simple equal-weight strategy when buying and selling, disregarding firm fundamentals. 

## Task
In lesson 6, we have seen that the Sante Fe artificial stock market used an interesting equation to determine prices, namely:

![equation](images/ex5-equation.jpg "Santa Fe Pricing Equation")

(Source: “Building the Santa Fe Artificial Stock Market”, 2002, Blake LeBaron)

In mathematical models, prices are usually determined by market clearing conditions. Here, the price is determined by supply (S, sellers) and demand (B, buyers), and a parameter lambda. Your task is to verify statistically how well this equation works in our simulation. To do so, I first suggest to reformulate the equation a little:

$\delta p = p_{t} - p_{t-30} = \lambda (\Sum_{i=t-30}^{t} B_i - S_i)$

This is essentially the same, but eliminating random noise by looking at a timeframe of 30 days as well as trying to explain the price difference instead of the raw price. When regressing the original equation, one would get an extremely high explanatory power due to the fact that the price today is an excellent predictor for the price tomorrow. However, I want you to know how well the net inflow predicts price *changes*. That's what the above equation can be used for.

What do you find? How well can the 30-day net inflow explain 30-day price changes in this setting? 

## Deadline

The deadline for submitting your lab journal to github is 2019-11-14 at 24:00.
Note that this is the last exercise. From now on, you can fully focus on your final presentations.
