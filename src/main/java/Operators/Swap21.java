package Operators;

import Common.Problem;
import Common.Route;
import Common.Solution;
import Constraints.HardConstraint;
import Utils.RandomController;

import java.util.ArrayList;

public class Swap21 extends Operator {
    public Swap21(Problem problem) {
        super(problem);
    }

    @Override
    public void singleOperate(Solution solution, OperationContext context) {
        context.mainRoute.swap21(context.sideRoute, context.operatePos[0], context.operatePos[1]);
    }

    @Override
    public void doOperateAll(Solution solution) {
        OperationContext context = new OperationContext.Builder(problem, OperationContext.operatorType.Swap21).
                setOperatePos(new Integer[2]).build();
        for (Route mainRoute : solution.getRoutes()) {
            context.setMainRoute(mainRoute);
            for (Route sideRoute : solution.getRoutes()) {
                context.setSideRoute(sideRoute);
                if (mainRoute == sideRoute) {
                    for (int i = 0; i < mainRoute.length() - 1; i++) {
                        context.setOperatePos(0, i);
                        for (int j = i + 3; j < mainRoute.length(); j++) {
                            context.setOperatePos(1, j);
                            HardConstraint.ConsStatus status = hardConstraintManager.fulfilled(context);
                            double costChg = softConstraintManager.fulfilled(context);
                            if (status == HardConstraint.ConsStatus.FULFILLED && costChg < 0) {
                                singleOperate(solution, context);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < mainRoute.length() - 1; i++) {
                        context.setOperatePos(0, i);
                        for (int j = 0; j < sideRoute.length(); j++) { //插入在指定节点之后
                            context.setOperatePos(1, j);
                            HardConstraint.ConsStatus status = hardConstraintManager.fulfilled(context);
                            double costChg = softConstraintManager.fulfilled(context);
                            if (status == HardConstraint.ConsStatus.FULFILLED && costChg < 0) {
                                singleOperate(solution, context);
                                if (i >= mainRoute.length() - 1) break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void doOperateBest(Solution solution) {

    }

    @Override
    public void doOperateRandom(Solution solution, double threshold) {
        OperationContext context = new OperationContext.Builder(problem, OperationContext.operatorType.Swap21).
                setOperatePos(new Integer[2]).build();
        for (Route mainRoute : solution.getRoutes()) {
            context.setMainRoute(mainRoute);
            ArrayList<Integer> mainRnd = RandomController.randIndex(mainRoute.length() - 1);
            for (Route sideRoute : solution.getRoutes()) {
                context.setSideRoute(sideRoute);
                ArrayList<Integer> sideRnd = RandomController.randIndex(sideRoute.length());
                if (mainRoute == sideRoute) {
                    for (int i : mainRnd) {
                        context.setOperatePos(0, i);
                        for (int j : mainRnd) {
                            if (j < i + 3) continue;
                            context.setOperatePos(1, j);
                            HardConstraint.ConsStatus status = hardConstraintManager.fulfilled(context);
                            double costChg = softConstraintManager.fulfilled(context);
                            if (status == HardConstraint.ConsStatus.FULFILLED && costChg < 0) {
                                singleOperate(solution, context);
                                return;
                            }
                        }
                    }
                } else {
                    for (int i : mainRnd) {
                        context.setOperatePos(0, i);
                        for (int j : sideRnd) { //插入在指定节点之后
                            context.setOperatePos(1, j);
                            HardConstraint.ConsStatus status = hardConstraintManager.fulfilled(context);
                            double costChg = softConstraintManager.fulfilled(context);
                            if (status == HardConstraint.ConsStatus.FULFILLED && costChg < 0) {
                                singleOperate(solution, context);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
