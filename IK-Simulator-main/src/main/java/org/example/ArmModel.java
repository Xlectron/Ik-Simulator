package org.example;

public class ArmModel {
    //Distances all in mm
    public final double LOWERARMLENGTH = 421;
    public final double UPPERARMLENGTH = 590;

    //Information About motor
    private final int TICKSPERREVOLUTION = 3895;
    private final double GEARRATIO = (26.0/28.0);

    //Coordinates of the base
    private final int baseX = 0;
    private final int baseY = 0;

    //Start Angle in degrees
    public final int UPPERARMSTARTANGLE = 90;
    public final int LOWERARMSTARTANGLE = 90;


    //Joint Limits
    private final int minInnerElbowAngle = 30;

    public int radiansToEncoder(double radians, boolean elbow){
        double revolutions = radians / (2 * Math.PI);
        int ticks = (int) (revolutions * TICKSPERREVOLUTION);
        if(elbow) ticks *= GEARRATIO;
//        System.out.println(ticks + " " + ticks % TICKSPERREVOLUTION);
        return ticks;
    }


    /**
     * @param targetX
     * @param targetY
     * @return
     */
    public double[] calculateMotorPositions(int targetX, int targetY) {
        //Calculate distance to target
        double distance = Math.sqrt(Math.pow(targetX - baseX, 2) + Math.pow(targetY - baseY, 2));
        if(distance> (LOWERARMLENGTH + UPPERARMLENGTH)){
            return null;
        }
        else{
            //Calculate Angle of Hypotenuse
            double hypotenuseAngle = Math.atan2(targetY - baseY, targetX - baseX);
            //Calculate angle of elbow using law of cosines
            double innerElbowAngle = Math.acos((Math.pow(LOWERARMLENGTH, 2) + Math.pow(UPPERARMLENGTH, 2) - Math.pow(distance, 2)) / (2 * LOWERARMLENGTH * UPPERARMLENGTH));
            double lowerMotorAngle;
            double upperMotorAngle;
            double wristRoll;
            double wristPitch;

            //Find the motor angles (if statement to make sure the arm goes overarm)
            if (targetX < baseX) {
                wristRoll = -1;
                //Calculate lower motor angle
                lowerMotorAngle = hypotenuseAngle - Math.asin((Math.sin(innerElbowAngle) * UPPERARMLENGTH) / distance);
                //Calculate upper motor angle
                upperMotorAngle = Math.PI - innerElbowAngle + lowerMotorAngle;
                //Calculate wrist pitch (0-1)
//                wristPitch = (Math.PI*2) - lowerMotorAngle - (Math.PI/2);
                wristPitch = (Math.PI)-upperMotorAngle;
//                wristPitch = map(upperMotorAngle, Math.PI/4, 3*Math.PI/2, 0, 1) - 0.1;

            }
            else {
                wristRoll = 1;
                //Calculate lower motor angle
                lowerMotorAngle = hypotenuseAngle + Math.asin((Math.sin(innerElbowAngle) * UPPERARMLENGTH) / distance);
                //Calculate upper motor angle
                upperMotorAngle = Math.PI - (Math.PI - innerElbowAngle) - (Math.PI - lowerMotorAngle);
                //Calculate wrist pitch (0-1)
                wristPitch =  (Math.PI*2)-upperMotorAngle;
//                wristPitch = 1-map(upperMotorAngle, -3*Math.PI/4, Math.PI, -1, 1) - 0.1;
            }

            //calculate target encoder position
            //First convert to revolutions
            upperMotorAngle -= Math.toRadians(UPPERARMSTARTANGLE);
            lowerMotorAngle -= Math.toRadians(LOWERARMSTARTANGLE);

            int upperMotorPosition = radiansToEncoder(upperMotorAngle, true);
            int lowerMotorPosition = radiansToEncoder(lowerMotorAngle, false);

            if(targetY < baseY && targetX < baseX){
                upperMotorPosition += TICKSPERREVOLUTION * GEARRATIO;
                lowerMotorPosition += TICKSPERREVOLUTION;
            }

            return new double[]{lowerMotorPosition, upperMotorPosition, wristPitch, wristRoll};
        }
    }

    public double encoderToDegrees(double encoder, boolean elbow){
        double degrees = (encoder * (360.0 / TICKSPERREVOLUTION));
        if(elbow) degrees /= GEARRATIO;
        return degrees;
    }

    public double encoderToRadians(double encoder, boolean elbow){
        return (encoderToDegrees(encoder, elbow) * Math.PI / 180.0);
    }

    public double map(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
    public int[] anglesToPosition(float shoulderRot, float elbowRot){
        //Using forward-kinematics, the position can be calculated
        int elbowX = (int) (LOWERARMLENGTH * Math.cos(Math.toRadians(shoulderRot)));
        int elbowY = (int) (LOWERARMLENGTH * Math.sin(Math.toRadians(shoulderRot)));

        int wristX = (int) (elbowX + UPPERARMLENGTH * Math.cos(Math.toRadians(elbowRot)));
        int wristY = (int) (elbowY + UPPERARMLENGTH * Math.sin(Math.toRadians(elbowRot)));
        return new int[]{wristX, wristY};
    }
}
