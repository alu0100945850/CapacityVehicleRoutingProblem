package daa.project.crvp.problem;

import java.util.ArrayList;

/**
 * TODO: DESCRIPTION
 * 
 * @author Carlos Dominguez Garcia (alu0100966589)
 * @version 1.0.0
 * @since 1.0.0 (Apr 18, 2018)
 * @file InterouteSwap.java
 *
 */
public class CVRPSolution {
    
    /**
     * Separator between routes
     */
    public static final int SEPARATOR = -1;
    
    /**
     * Reference to a data structure holding the information
     * of the specific problem that this solution is for.
     */
    private CVRPSpecification problemInfo;
    
    /**
     * Solution representation. The codification is k sequences of numbers
     * separated by a SEPARATOR. The i-th sequence represents the sequence
     * of clients that the i-th vehicle have to send supplies to. Being each
     * element of the sequence the ID of the client to visit
     */
    private ArrayList<Integer> vehicleRoutes = new ArrayList<>();
    
    /**
     * List of k numbers representing the index at vehicleRoutes where
     * the i-th route starts
     */
    private ArrayList<Integer> routesStartingIndexes = new ArrayList<>();

    /**
     * In the i-th position is stored the remaining capacity that
     * the i-th vehicle can carry
     */
    private ArrayList<Integer> vehicleRemainingCapacities = new ArrayList<>();
    
    /**
     * Total distance that have to travel to get to every client
     */
    private int totalDistance;
    
    /**
     * Whether this solution is feasible or not
     */
    private boolean isFeasible;
    
    /**
     * TODO: desc
     */
    public CVRPSolution(CVRPSpecification problemInfo, ArrayList<Integer> vehicleRoutes) {
        setProblemInfo(problemInfo);
        setVehicleRoutes(vehicleRoutes);
        
        final int vehiclesCapacity = getProblemInfo().getCapacity();
        int currentRouteStartingIndex = 0;
        int currentRouteDemand = 0;
        int totalDistance = 0;
        setFeasible(true);
        CVRPClient prevClientOfTheRoute = getProblemInfo().getDepot();
        CVRPClient currentClientOfTheRoute = null;
        
        for (int pos = 0; pos < getVehicleRoutes().size(); ++pos) {
            int clientId = getVehicleRoutes().get(pos);
            if (clientId == CVRPSolution.SEPARATOR) {
                // Add remaining capacity for the previous route. And set the demand for the next potential route
                addVehicleRemainingCapacity(vehiclesCapacity - currentRouteDemand);
                currentRouteDemand = 0;
                
                // Add starting index for the previous route. And set the index for the next potential route
                addRoutesStartingIndex(currentRouteStartingIndex);
                currentRouteStartingIndex = pos + 1;
                
                // Update lastClientOfTheRoute to be depot (so next route starts fresh from depot)
                prevClientOfTheRoute = getProblemInfo().getDepot();
            } else {
                // Get current client
                currentClientOfTheRoute = getProblemInfo().getClient(clientId);
                
                // Update demand and feasibility if current vehicle has to satisfy more demand
                // than it can
                currentRouteDemand += currentClientOfTheRoute.getDemand();
                if (currentRouteDemand > vehiclesCapacity) {
                    setFeasible(false);
                }
                
                // Update total distance
                totalDistance += CVRPClient.euclideanDistance(prevClientOfTheRoute, currentClientOfTheRoute);
                
                // Update last client of the route to be the current one
                prevClientOfTheRoute = currentClientOfTheRoute;
            }
        }
        
        setTotalDistance(totalDistance);
    }
    
    /**
     * Returns the client ID in the specified position or SEPARATOR if there is
     * no client in that position. Throws if the position is off limits
     * 
     * @param position  Position in the solution vector specifying the client ID to return
     * @return  Client ID of the client in the specified position of SEPARATOR
     */
    public int getClientId(int position) {
        if (position < 0 || position >= getVehicleRoutes().size()) {
            throw new IndexOutOfBoundsException("invalid solution index \"" + position
                    + "\" Expected index to be 0 <= index < " + getVehicleRoutes().size());
        }
        return getVehicleRoutes().get(position);
    }
    
    /**
     * Returns the client information in the specified position of the solution.
     * Or null if at the specified position there is not a valid client ID.
     * Or throws if the position specified is off limits.
     * 
     * @param position  Index in the solution array
     * @return  Client information
     */
    public CVRPClient getClient(int position) {
        int clientId = getClientId(position);
        if (clientId == CVRPSolution.SEPARATOR) {
            return null;
        }
        return getProblemInfo().getClient(clientId);
    }
    
    /**
     * Returns the information of the j-th client in the i-th route. Where
     * j is the positionInRoute and i the route. If there is no valid client
     * at the position specified, null is returned. Exception is thrown if
     * any position is off limits
     * 
     * @param route Route to get the client information from
     * @param positionInRoute   Position of the client information to get inside the specified route
     * @return Information of the specified client
     */
    public CVRPClient getClient(int route, int positionInRoute) {
        int routeStartingIndex = getRouteStartingIndex(route);
        try {
            return getClient(routeStartingIndex + positionInRoute);
        } catch (IndexOutOfBoundsException error) {
            throw new IndexOutOfBoundsException("invalid solution index \""
                    + positionInRoute + "\" for route number \"" + route);
        }
    }
    
    /** @return the problemInfo */
    public CVRPSpecification getProblemInfo() {
        return problemInfo;
    }
    
    /** @return the totalDistance */
    public int getTotalDistance() {
        return totalDistance;
    }
    
    /** @return the isFeasible */
    public boolean isFeasible() {
        return isFeasible;
    }
    
    /**
     * Returns the starting index of the specified route in this solution
     * 
     * @param route Route number to get the starting index for
     * @return  The starting index of the route in this solution
     */
    public int getRouteStartingIndex(int route) {
        return getRoutesStartingIndexes().get(route);
    }
    
    /**
     * Returns the number of routes
     * 
     * @return the number of routes
     */
    public int getNumberOfRoutes() {
        return getRoutesStartingIndexes().size();
    }
    
    /**
     * Returns the number of clients that the specified route has
     * 
     * @param route Route number to get its number of vehicles
     * @return  Number of vehicles that the specified route has
     */
    public int getNumberOfClientsInRoute(int route) {
        int routeStartingIndex = getRoutesStartingIndexes().get(route);
        // If the route specified is the last route...
        if (route == getNumberOfRoutes()) {
            return getVehicleRoutes().size() - routeStartingIndex - 1;
        } else {
            int nextRouteStartingIndex = getRoutesStartingIndexes().get(route + 1);
            return nextRouteStartingIndex - routeStartingIndex - 1;
        }
    }
    
    /** @return the vehicleRoutes */
    private ArrayList<Integer> getVehicleRoutes() {
        return vehicleRoutes;
    }
    
    /** @return the routesStartingIndexes */
    private ArrayList<Integer> getRoutesStartingIndexes() {
        return this.getRoutesStartingIndexes();
    }

    /** @return the vehicleRemainingCapacities */
    private ArrayList<Integer> getVehicleRemainingCapacities() {
        return vehicleRemainingCapacities;
    }
    
    /** @param problemInfo the problemInfo to set */
    private void setProblemInfo(CVRPSpecification problemInfo) {
        this.problemInfo = problemInfo;
    }
    
    /** @param vehicleRoutes the vehicleRoutes to set */
    private void setVehicleRoutes(ArrayList<Integer> vehicleRoutes) {
        this.vehicleRoutes = vehicleRoutes;
    }
    

    private void addRoutesStartingIndex(int newRouteStartingIndex) {
        this.routesStartingIndexes.add(newRouteStartingIndex);
    }
    
    private void addVehicleRemainingCapacity(int newVehicleRemainingCapacity) {
        this.vehicleRemainingCapacities.add(newVehicleRemainingCapacity);
    }
    
    /** @param totalDistance the totalDistance to set */
    private void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    /** @param isFeasible the isFeasible to set */
    private void setFeasible(boolean isFeasible) {
        this.isFeasible = isFeasible;
    }
    
}