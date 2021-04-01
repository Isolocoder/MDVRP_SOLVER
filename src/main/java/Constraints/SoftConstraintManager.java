package Constraints;

import IO.ConstraintsConfigReader;
import Operators.OperationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoftConstraintManager extends SoftCostConstraint{
    public static final ArrayList<String> constraints2Load = new ConstraintsConfigReader().readConfig().softConstraints;
    protected List<SoftConstraint> constraints;
    private static final HashMap<String, SoftConstraintManager> mapper = new HashMap<>();

    public static SoftConstraintManager getInstance(Class<?> clazz){
        String name = clazz.getName();
        name = name.substring(name.lastIndexOf('.')+1);
        return getInstance(name);
    }

    public static SoftConstraintManager getInstance(String className){
        if (!mapper.containsKey(className)) {
            mapper.put(className, new SoftConstraintManager(className));
        }
        return mapper.get(className);
    }

    private SoftConstraintManager(String className){
        constraints = new ArrayList<>();
        try {
            for(String contraintName:constraints2Load) {
                Class<?> clazz = Class.forName(String.format("Constraints.%sConstraints.%sSoft%sConstraint",
                        className, className, contraintName));
                SoftConstraint constraint = (SoftConstraint) clazz.getConstructor().newInstance();
                constraints.add(constraint);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public double fulfilled(OperationContext context) {
        double cost = 0;
        for(SoftConstraint constraint:constraints){
            cost+=constraint.fulfilled(context);
        }
        return cost;
    }
}