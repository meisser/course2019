package com.agentecon.production;

import com.agentecon.consumer.Weight;
import com.agentecon.goods.Good;
import com.agentecon.goods.Inventory;
import com.agentecon.goods.Quantity;

/**
 * A production function transforms inputs into outputs.
 * It can only be used once per day.
 */
public interface IProductionFunction {

	/**
	 * Returns the input goods for this production function, including
	 * both transformed and capital goods.
	 * 
	 * Use 'getWeight' to find out which inputs are capital goods.
	 */
	public Good[] getInputs();
	
	/**
	 * Returns the relative weight of the given input goods and
	 * whether they are capital goods or transformed goods.
	 */
	public Weight getWeight(Good good);
	
	/**
	 * Returns the number of units of the given good that are needed
	 * upfront before an output can be produced.
	 */
	public double getFixedCost(Good good);

	/**
	 * Returns the amount that needs to be spent on inputs to cover all fixed costs.
	 * Assuming the provided prices are correct, trying to spend less than that is pointless.
	 * @throws PriceUnknownException 
	 */
	public double getFixedCosts(IPriceProvider prices) throws PriceUnknownException;

	/**
	 * Returns the output produced by this production function.
	 */
	public Good getOutput();

	/**
	 * The production function is applied to the goods found in the inventory
	 * and resulting output added to the inventory.
	 * 
	 * Capital might be needed, but is not depleted.
	 * Other inputs are fully depleted. If you do not want to fully use the inputs,
	 * use inventory.hide to create a delegate inventory that hides some of its
	 * goods.
	 * 
	 * This function must only be called once per day!
	 * 
	 * @return the amount of the output produced
	 */
	public Quantity produce(Inventory inventory);
	
	/**
	 * Calculates the hypothetical output from the given input quantities, but does
	 * not actually produce anything.
	 */
	public Quantity calculateOutput(Quantity... inputs);
	
	/**
	 * Returns how much should optimally be spent on the input goods given the provided
	 * price beliefs.
	 * Capital inputs are assumed as given and provided through inventory.
	 * @throws PriceUnknownException 
	 */
	public double getCostOfMaximumProfit(Inventory inv, IPriceProvider prices) throws PriceUnknownException;
	
	/**
	 * Tells how how much to optimally spend on each input good given total spendings.
	 * Capital inputs are not taken into account.
	 * @throws PriceUnknownException 
	 */
	public double getExpenses(Good good, IPriceProvider prices, double totalSpendings) throws PriceUnknownException;
	
}
