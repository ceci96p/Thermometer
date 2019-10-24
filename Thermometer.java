public class Thermometer {

    private int temperatureMode;
    private boolean repeatAlert;
    private boolean tolerateFluctuations;
    private boolean direction;
    private double threshold;

    static double[] test0 = {5,0.5,0,-0.5,-1,-0.5,0.5,0.3,0,-5,-1,8,-5};
    static double[] test1 = {70,79.8,80,85,80.5,79.5,79.8,90};


    public static void main(String[] args) {

        Thermometer case1 = new Thermometer(0,true,true,true, 0);
        case1.on(test0,0);
        case1.on(test1,1);
        case1.on(test0,1);
        case1.on(test1,0);

        case1.resetThreshold(0.3);
        case1.on(test0,1);

        Thermometer case2 = new Thermometer(0,true,true,false,80);
        case2.on(test1,1);

        Thermometer case3 = new Thermometer(1,true,false,true,0);
        case3.on(test1,1);

        Thermometer case4 = new Thermometer(1,true,false,false,80);
        case4.on(test1,1);

        Thermometer case5 = new Thermometer(1,false,true,true,0);
        case5.on(test0,1);

        Thermometer case6 = new Thermometer(2,false,true,false,80);
        case6.on(test0,1);

        Thermometer case7 = new Thermometer(1,false,false,true,0);
        case7.on(test0,1);

        Thermometer case8 = new Thermometer(1,false,false,false,0);
        case8.on(test0,1);

        double[] invertedTest0 = new double [test0.length];
        for (int i = 0 ;i < test0.length; i++){
            double newUnit = case8.convertUnit(test0[i],1);
            invertedTest0[i] = newUnit;
        }
        Thermometer case9 = new Thermometer(0,false,false,true,0);
        case9.on(invertedTest0,0);
    }

    /**
     * Constructor
     * @param temperatureMode input that defines what temperature unit the thermometer will output.
     *                        0 = Fahrenheit.
     *                        1 = Celsius.
     *                        2 = both Fahrenheit and Celsius.
     * @param repeatAlert input that defines how often the threshold alert will be given.
     *                    true = Every time it reaches the threshold.
     *                    false = Only the first time it reaches the threshold.
     * @param tolerateFluctuations input that defines whether a fluctuation change is accepted after the threshold has been reached and defines
     *                            if the current temperature is still within the the threshold value taking into consideration these fluctuations.
     *                             true = It tolerates fluctuations after reaching initial threshold.
     *                             false = It does not tolerate fluctuations after reaching threshold.
     * @param direction input that defines whether the alert will be given in relation to the temperature change direction.
     *                  true = It only alerts if temperature moves right to left.
     *                  false = It Only alerts if temperature moves left to right.
     * @param threshold reference temperature value used to check if it has been reached or surpassed
     *                  IMPORTANT: threshold should be defined in relation to the temperature unit type of which data will be read as.
     */

    public Thermometer(int temperatureMode,boolean repeatAlert,boolean tolerateFluctuations, boolean direction, double threshold){
        this.temperatureMode = temperatureMode;
        this.repeatAlert = repeatAlert;
        this.tolerateFluctuations = tolerateFluctuations;
        this.direction = direction;
        this.threshold = threshold;
    }

    /**
     * Resets the threshold value of a thermometer.
     * @param newThreshold new threshold value.
     */

    public void resetThreshold(double newThreshold) {
        this.threshold = newThreshold;
    }

    /**
     * Yields result of a thermometer working under specified behavior properties.
     * @param data array of data acting as the temperature measurements being acquired.
     * @param unit input that defines what type of temperature unit the data array is being read as either 0 = Fahrenheit and  1 = Celsius.
     */



    public void on(double[] data, int unit){
        double previousDataValue = 0;
        boolean gottenToThreshold = false;

        for(int i=0;i< data.length; i++) {
            chooseTemperature(data[i], unit);
            boolean result = executingConditions(data[i], previousDataValue,gottenToThreshold);
            gottenToThreshold = result;
            previousDataValue = data [i];
        }

        System.out.println("END");
    }


    private boolean executingConditions(double value, double previousDataValue, boolean gottenToThreshold) {
        double tolerance = 0.5;

        if (this.repeatAlert) {
            if (this.tolerateFluctuations) {
                if (this.direction) {
                    return behaviourToRepeatTolerateAndRight(value,previousDataValue,gottenToThreshold,tolerance);
                } else if (!direction) {
                    return behaviourToRepeatTolerateAndLeft(value,previousDataValue,gottenToThreshold,tolerance);
                }
            } else if (!tolerateFluctuations) {
                if (direction) {
                    return behaviourToRepeatNotTolerateAndRight(value,previousDataValue);
                } else if (!direction) {
                    return behaviourToRepeatNotTolerateAndLeft(value,previousDataValue);
                }
            }
        } else if (!repeatAlert) {
            if (tolerateFluctuations) {
                if (direction) {
                    return behaviourNotRepeatTolerateAndRight(value,previousDataValue,gottenToThreshold,tolerance);
                } else if (!direction) {
                    return behaviourNotRepeatTolerateAndLeft(value,previousDataValue,gottenToThreshold,tolerance);
                }
            } else if (!tolerateFluctuations) {
                if (direction) {
                    return behaviourNotRepeatNotTolerateAndRight(value,previousDataValue, gottenToThreshold);
                } else if (!direction) {
                    return behaviourNotRepeatNotTolerateAndLeft(value,previousDataValue, gottenToThreshold);
                }
            }
        }
        return true;
    }

    private boolean behaviourToRepeatTolerateAndRight(double value, double previousDataValue, boolean gottenToThreshold, double tolerance){
        if ((value <= this.threshold || (value <= this.threshold + tolerance && gottenToThreshold))
                && ((previousDataValue == 0 || (previousDataValue != 0 && value <= previousDataValue)))) {
            System.out.println("You have reached the threshold \n");
            gottenToThreshold = true;
        }
        if (value > this.threshold + tolerance) {
            gottenToThreshold = false;

        }return gottenToThreshold;
    }

    private boolean behaviourToRepeatTolerateAndLeft(double value, double previousDataValue, boolean gottenToThreshold, double tolerance){
        if ((value >= this.threshold || value >= this.threshold - tolerance && gottenToThreshold)
                && ((previousDataValue == 0 || (previousDataValue != 0 && value >= previousDataValue)))) {
            System.out.println("You have reached the threshold \n");
            gottenToThreshold = true;
        }
        if (value < this.threshold - tolerance) {
            gottenToThreshold = false;
        }return gottenToThreshold;
    }

    private boolean behaviourToRepeatNotTolerateAndRight(double value, double previousDataValue){
        if (value <= threshold && (previousDataValue == 0 || (previousDataValue != 0 && value <= previousDataValue))) {
            System.out.println("You have reached the threshold\n");
        }
        return true;
    }

    private boolean behaviourToRepeatNotTolerateAndLeft(double value, double previousDataValue){
        if (value >= threshold && (previousDataValue == 0 || (previousDataValue != 0 && value >= previousDataValue))) {
            System.out.println("You have reached the threshold\n");
        }
        return true;
    }

    private boolean behaviourNotRepeatTolerateAndRight(double value, double previousDataValue, boolean gottenToThreshold, double tolerance){
        if ((value <= threshold || value <= threshold + tolerance && gottenToThreshold)
                && (previousDataValue == 0 || (previousDataValue != 0 && value <= previousDataValue))
                && !gottenToThreshold) {
            System.out.println("You have reached the threshold\n");
            gottenToThreshold = true;
        }
        if (value > threshold + tolerance) {
            gottenToThreshold = false;
        }return gottenToThreshold;
    }

    private boolean behaviourNotRepeatTolerateAndLeft (double value, double previousDataValue, boolean gottenToThreshold, double tolerance){
        if ((value >= threshold || value >= threshold - tolerance && gottenToThreshold)
                && (previousDataValue == 0 || (previousDataValue != 0 && value >= previousDataValue))
                && !gottenToThreshold) {
            System.out.println("You have reached the threshold\n");
            gottenToThreshold = true;
        }
        if (value < threshold - tolerance) {
            gottenToThreshold = false;

        }return gottenToThreshold;
    }

    private boolean behaviourNotRepeatNotTolerateAndRight(double value, double previousDataValue, boolean gottenToThreshold){
        if (value <= threshold && (previousDataValue == 0 || (previousDataValue != 0 && value <= previousDataValue)) && !gottenToThreshold) {
            System.out.println("You have reached the threshold\n");
            gottenToThreshold = true;
        }
        if (value > threshold) {
            gottenToThreshold = false;

        }return gottenToThreshold;
    }

    private boolean behaviourNotRepeatNotTolerateAndLeft(double value, double previousDataValue, boolean gottenToThreshold){
        if (value >= threshold && (previousDataValue == 0 || (previousDataValue != 0 && value >= previousDataValue)) && !gottenToThreshold) {
            System.out.println("You have reached the threshold\n");
            gottenToThreshold = true;
        }
        if (value < threshold) {
            gottenToThreshold = false;
        }return gottenToThreshold;
    }

    private void chooseTemperature(double value, int unit) {
        double conversionValue = 0;
        if(this.temperatureMode != unit) {
            if(unit==0) {
                conversionValue = convertUnit(value, 0);
            } else{ conversionValue = convertUnit(value, 1);
            }
        }
        printTemperature(value,conversionValue,unit);
    }

    /**
     * Based on unit given being Celsius or Fahrenheit it will convert to the other measurement it is not it being either Celsius or Fahrenheit.
     * @param currentTemp numeric value given for conversion.
     * @param unit unit of temperature of the inputted value.
     * @return opposite value of the one given being Celsius or Fahrenheit.
     */
    public double convertUnit(double currentTemp, int unit){
        double conversionValue = 0;
        if (unit==1){
            conversionValue = (currentTemp * 1.8 + 32);
        } else if (unit== 0){
            conversionValue = (((currentTemp - 32) * 5)/9);
        }
        return conversionValue;
    }


    private void printTemperature(double currentTemp, double conversionValue, int unit) {
        if (this.temperatureMode == 0) {

            if (unit == this.temperatureMode) {
                System.out.println("Your current temperature is " + currentTemp + " Fahrenheit\n");
            } else {
                System.out.println("Your current temperature is " + conversionValue + " Fahrenheit\n");
            }

        } else if (this.temperatureMode == 1) {

            if (unit == this.temperatureMode) {
                System.out.println("Your current temperature is " + currentTemp + " Celsius\n");
            } else {
                System.out.println("Your current temperature is " + conversionValue + " Celsius\n");
            }

        } else if (this.temperatureMode == 2) {

            if (unit == 0) {
                System.out.println("Your current temperature is " + conversionValue + " Celsius or " + currentTemp + " Fahrenheit\n");
            } else if (unit == 1){
                System.out.println("Your current temperature is " + currentTemp + " Celsius or " + conversionValue + " Fahrenheit\n");
            }
        }
    }
}


