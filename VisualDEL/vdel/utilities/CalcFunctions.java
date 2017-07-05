package vdel.utilities;


public class CalcFunctions {

    public Double sin(Double angle) {
        
        return new Double(Math.sin(angle *(Math.PI/180)));
    }

    public Double cos(Double angle) {

        return new Double(Math.cos(angle));
    }

    public Double tan(Double angle) {

        return new Double(Math.tan(angle));
    }

    public Double arcSinh( Double ratio ){
        return new Double( Math.log( ratio + Math.sqrt( Math.pow( ratio, 2 ) + 1 ) ) );
    }//end method arcsinh

    public Double arcCosh( Double ratio ){
        return new Double( Math.log( ratio + Math.sqrt( Math.pow( ratio, 2 ) - 1 ) ) );
    }//end method arccosh

    public Double arcTanh( Double ratio ){
        return new Double(Math.log( Math.sqrt( ( 1 + ratio )/( 1 - ratio ) ) ));
    }//end method arctanh

    public Double natLog( Double ratio ){
        return new Double( Math.log( ratio ) );
    }//end method ln

    public Double eExp( Double ratio ){
        return new Double( Math.exp( ratio ) );
    }//end method exp	

    public Double arcSin(Double ratio) {

        return new Double(Math.toDegrees(Math.asin(ratio)));
    }
    public Double arcTan(Double ratio) {

        return new Double(Math.toDegrees(Math.atan(ratio)));
    }

    public Double arcCos(Double ratio) {

        return new Double(Math.toDegrees(Math.acos(ratio)));
    }

    public Double tanh(Double angle) {
        return new Double(Math.tanh(angle.doubleValue()));
    }

    public Double sinh(Double angle) {
        return new Double(Math.sinh(angle.doubleValue()));
    }

    public Double cosh(Double angle) {
        return new Double(Math.cosh(angle.doubleValue()));
    }

    public Double log(Double arg) {

        return new Double(Math.log10(arg));
    }

    public Double antiLog(Double arg) {

        return new Double(Math.pow( 10, arg));
    }

    public Double factorial(Integer arg) {
        Integer fact = 1;
        for (; arg > 1; arg--) {
            fact = fact * arg;
        }
        return new Double(fact.intValue());
    }

    public Double xPowY(Double number, Double pow) {

        return new Double(Math.pow(number, pow));
    }

    public Double xRootY(Double x, Double y) {
        double temp = Math.log10(y) / x;
        return new Double(Math.pow(10, temp));
    }
    
        
    public Double percent(Double arg) {

        return new Double(arg / 100);
    }

    public Double reciprocal(Double arg) {

        return new Double(1 / arg);
    }

//    public Integer permutation(Integer arg1, Integer arg2) {
//        
//        int perm = arg1.intValue();
//	int result = 1;
//	for(int i = 1;  i <= arg2.intValue() ; i++){
//            result = result * perm;
//            perm = perm - 1;
//	}
//	return new Integer(result);
//    }
    
    public Double permutation(Integer arg1, Integer arg2) {
        return new Double(factorial(arg1) / factorial (arg1 - arg2));
    }
    
//    public Integer combination(Integer arg1, Integer arg2) {
//	
//        int perm = arg1.intValue();
//	int result = 1;
//	for(int i = 1;  i <= arg2.intValue() ; i++){
//		result = result * perm / i;
//		perm = perm - 1;
//	}
//	return new Integer(result);
//
//    }
    
    public Double combination(Integer arg1, Integer arg2){
        return new Double (permutation(arg1, arg2) / factorial(arg2));
    }
    
    public Double xSquare(Double arg) {

        return new Double(arg.doubleValue() * arg.doubleValue());
    }
    
    public Double xCube(Double arg) {

        return new Double(arg * arg * arg);
    }

    public Double squareRoot(Double arg) {

        return new Double(Math.sqrt(arg.doubleValue()));
    }
    
    public Double cubeRoot(Double arg) {

        return new Double(Math.cbrt(arg));
    }
     
     public static void main(String args[]) {
         CalcFunctions tester = new CalcFunctions();
         double res = tester.arcCosh(100.0).doubleValue();
         double result = tester.combination(7,3);
         System.out.println(res);
         System.out.println(result);
         
     }
    
}
