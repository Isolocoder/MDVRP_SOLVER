package Algorithm;

import Common.Node.Customer;
import Common.Node.Depot;
import Common.Node.Node;
import Common.Problem;
import Common.Route;
import Common.Solution;
import Constraints.HardConstraint;
import Constraints.HardConstraintManager;
import Constraints.InsertionConstraints.SoftCostConstraintImpl;
import Constraints.SoftConstraint;
import Operators.OperationContext;

import java.util.ArrayList;
import java.util.Collections;

public class GreedyGenerator extends Generator{

    SoftConstraint costConstraint = new SoftCostConstraintImpl();
    HardConstraintManager constraintManager = HardConstraintManager.getInstance("Insertion");

    public GreedyGenerator(Problem problem) {
        super(problem);
    }

    @Override
    public Solution build() {
        Solution solution = initSolution();
        executeGreedyAlgo(solution);
        return solution;
    }


    private void executeGreedyAlgo(Solution solution){
        for (Node node:solution.unassignedCustomer){
            Customer customer = (Customer) node;
            OperationContext context = new OperationContext.Builder(problem, OperationContext.operatorType.INSERT).
            setOperatePos(new Integer[1]).setOperateNodes(new Node[1]).build();
            OperationContext bestContext = null;
            double minInsertCost = Double.MAX_VALUE;
            for(Route route:solution.routes){
                context.setMainRoute(route).setOperateNodes(0,customer);
                for (int i = 0; i <= route.length(); i++) {
                    context.setOperatePos(0,i);
                    if (constraintManager.fulfilled(context)!= HardConstraint.ConsStatus.FULFILLED)continue;
                    double costChg = costOfInsertion(context);
                    if (minInsertCost > costChg){
                        minInsertCost = costChg;
                        bestContext = context.copy();
                    }
                }
            }

            double minRouteCost = Double.MAX_VALUE;
            Depot bestDepot = null;
            for(Depot depot:problem.depots){
                double costChg = costOfNewRoute(customer,depot);
                if (costChg < minRouteCost){
                    minRouteCost = costChg;
                    bestDepot = depot;
                }
            }
            if (bestContext==null&&bestDepot==null){
                logger.error("There's no valid operations, process exists");
            }
            if (minRouteCost < minInsertCost||bestContext==null){
                assert bestDepot!=null;
                Route route = new Route(new ArrayList<>(Collections.singletonList(customer)),bestDepot,bestDepot);
                solution.addRoute(route);
                        logger.debug(String.format("new route %d added:%d->%d->%d",
                                route.getId(), bestDepot.id, customer.id, bestDepot.id));
            }else {
                logger.debug(String.format("customer: %d insert to pos %d of route %d",
                        bestContext.operateNodes[0].id,bestContext.operatePos[0],bestContext.mainRoute.getId()));
                bestContext.mainRoute.addNode(bestContext.operatePos[0],bestContext.operateNodes[0]);
            }
        }
        solution.unassignedCustomer.clear();
    }


    protected Solution ruin(Solution solution){
        solution.shuffle();
        if (solution.routes.size()==0){
            logger.error("there's no routes in this solution");
            return solution;
        }
        solution.unassignedCustomer.addAll(solution.routes.get(solution.routes.size()-1).getRoute());
        solution.routes.remove(solution.routes.size()-1);
        return solution;
    }

    protected Solution recreate(Solution solution){
//        TODO recreate algo
        return null;
    }

    private double costOfInsertion(OperationContext context){
        return costConstraint.fulfilled(context);
    }

    private double costOfNewRoute(Customer customer,Depot depot){
        return this.problem.getDistance(customer,depot) * 2;
    }

    private void addRoute(Depot depot){
        this.routes.add(new Route(new ArrayList<>(),depot,depot));
    }
}
