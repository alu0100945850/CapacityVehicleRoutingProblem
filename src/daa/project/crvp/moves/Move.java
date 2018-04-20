package daa.project.crvp.moves;

import daa.project.crvp.problem.CVRPSolution;

/**
 * Representation of a movement. A movement is an operation to a given solution
 * that generates a "neighbor" solution.
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 18, 2018)
 * @file Move.java
 *
 */
public abstract class Move {
	/**
	 * Base solution from which a set of neighbors will be generated by applying
	 * this move
	 */
	private CVRPSolution solution;

	/** Updates the internal state to point to the next neighbor solution */
	protected abstract void nextNeighbor();

	/**
	 * @return The difference in the objective function that applying the last move
	 *         made
	 */
	protected abstract int getLastMoveCost();

	/**
	 * @return The objective function value of the solution made from applying the
	 *         last move
	 */
	protected abstract int getCost();

	/**
	 * @return Whether the solution made from applying the last move is feasible or
	 *         not
	 */
	protected abstract boolean isCurrentNeighborFeasible();

	/** @return The solution made from applying the last move */
	protected abstract CVRPSolution getCurrentNeighbor();

	/**
	 * Base solution from which the neighborhood structure will be generated
	 * 
	 * @param solution
	 *          Base solution
	 */
	protected void setSolution(CVRPSolution solution) {
		this.solution = solution;
	}

	/** @return The current solution that is the movement using. */
	protected CVRPSolution getSolution() {
		return solution;
	}
}
