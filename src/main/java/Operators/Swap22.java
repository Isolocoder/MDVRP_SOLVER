package Operators;

import Common.Problem;
import Common.Route;
import Common.Solution;
import Constraints.HardConstraint;

public class Swap22 extends BaseOperator{
    public Swap22(Problem problem) {
        super(problem);
    }

    @Override
    public void singleOperate(Solution solution, OperationContext context) {
        context.mainRoute.swap22(context.sideRoute,context.operatePos[0],context.operatePos[1]);
    }

    @Override
    public void doOperateAll(Solution solution) {
        OperationContext context = new OperationContext.Builder(problem, OperationContext.operatorType.Swap22).
                setOperatePos(new Integer[2]).build();
        for (Route mainRoute:solution.getRoutes()) {
            context.setMainRoute(mainRoute);
            for (Route sideRoute:solution.getRoutes()) {
                context.setSideRoute(sideRoute);
                if (mainRoute==sideRoute){
                    for (int i = 0; i < mainRoute.length()-1; i++) {
                        context.setOperatePos(0,i);
                        for (int j = i + 3; j < mainRoute.length() - 1; j++) {
                            context.setOperatePos(1,j);
                            HardConstraint.ConsStatus status = hardConstraintManager.fulfilled(context);
                            double costChg = softConstraintManager.fulfilled(context);
                            if (status == HardConstraint.ConsStatus.FULFILLED && costChg < 0){
                                singleOperate(solution, context);
                            }
                        }
                    }
                }else {
                    for (int i = 0; i < mainRoute.length()-1; i++) {
                        context.setOperatePos(0, i);
                        for (int j = 0; j < sideRoute.length()-1; j++) { //插入在指定节点之后
                            context.setOperatePos(1, j);
                            HardConstraint.ConsStatus status = hardConstraintManager.fulfilled(context);
                            double costChg = softConstraintManager.fulfilled(context);
                            if (status == HardConstraint.ConsStatus.FULFILLED && costChg < 0) {
                                singleOperate(solution, context);
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

    }
}