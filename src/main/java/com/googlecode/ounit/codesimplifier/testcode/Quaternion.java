package com.googlecode.ounit.codesimplifier.testcode;

/**
 * Quaternions. Basic operations.
 * @version 20.10.2014
 * @author Urmas Hoogma
 */
public class Quaternion  implements Cloneable {

   /** my instance variables */
   private double r,i,j,k;

   /** double numbers less than DELTA are considered zero */
   private static final double DELTA = 0.00000000000001;
   
   /** Constructor from four double values.
    * @param a real part
    * @param b imaginary part i
    * @param c imaginary part j
    * @param d imaginary part k
    */
   public Quaternion (double a, double b, double c, double d) {
      this.r = a;
      this.i = b;
      this.j = c;
      this.k = d;
   }

   public void setR(double r) {
      this.r = r;
   }

   public void setI(double i) {
      this.i = i;
   }

   public void setJ(double j) {
      this.j = j;
   }

   public void setK(double k) {
      this.k = k;
   }

   /** Real part of the quaternion.
    * @return real part
    */
   public double getRpart() {
      return this.r;
   }

   /** Imaginary part i of the quaternion.
    * @return imaginary part i
    */
   public double getIpart() {
      return this.i;
   }

   /** Imaginary part j of the quaternion.
    * @return imaginary part j
    */
   public double getJpart() {
      return this.j;
   }

   /** Imaginary part k of the quaternion.
    * @return imaginary part k
    */
   public double getKpart() {
      return this.k;
   }

   /** Conversion of the quaternion to the string.
    * @return a string form of this quaternion:
    * "a+bi+cj+dk"
    * (without any brackets)
    */
   @Override
   public String toString() {
      //return this.r+"+i"+this.i+"+j"+this.j+"+k"+this.k;
      return this.r+"+"+this.i+"i+"+this.j+"j+"+this.k+"k";
   }

   /** Conversion from the string to the quaternion.
    * Reverse to <code>toString</code> method.
    * @throws IllegalArgumentException if string s does not represent
    *     a quaternion (defined by the <code>toString</code> method)
    * @param s string of form produced by the <code>toString</code> method
    * @return a quaternion represented by string s
    */
   public static Quaternion valueOf (String s) {
	      String [] str = s.split("\\+");

	      Quaternion res = new Quaternion(0.,0.,0.,0.);
	      double aD = 0.0, bD = 0.0, cD = 0.0, dD = 0.0;
	      /**
	       * Kommentaar: Meetod valueOf võiks vigase sisendi korral
	       * kasutajasõbralikke veateateid anda.
	       * Urmas Hoogma: Nüüd on olemas, kusjuures n2idatakse k2tte koht kus viga
	       * tekkis.
	       */
	      double numericValue;
	      String imaginaryLetter = "";
	      String message="";
	      StringBuilder sb = new StringBuilder();
	      sb.append("Viga. Teie sisend "+s+" on korrektne ainult kuni kohani: ");

	      for (int i = 0; i < 4; i++) {
	         String actualLetter;
	         String actualNumber;

	         if (i==0) {
	            actualLetter = "";
	            actualNumber = str[i];
	         } else {
	        	int strlen = str[i].length();
	            actualLetter = str[i].substring(strlen-1, strlen);
	            actualNumber = str[i].substring(0, strlen-1);
	         }

	         if (i==0) {imaginaryLetter = "";}
	         if (i==1) {imaginaryLetter = "i";}
	         if (i==2) {imaginaryLetter = "j";}
	         if (i==3) {imaginaryLetter = "k";}
	         if (!isNumeric(actualNumber)) {
	           	 sb.append(". Viga tekkis sisendi "+actualNumber+ " tottu.");
	           	 message = sb.toString();
	           	 throw new IllegalArgumentException(message);
           	 } else {
	        	numericValue = Double.parseDouble(actualNumber);
	            sb.append(numericValue);
	            //sb.append("+");
	            if (!actualLetter.equals(imaginaryLetter)) {
		        	sb.append(". Viga tekkis sisendi "+imaginaryLetter+ " tottu.");
		            message = sb.toString();
		            throw new IllegalArgumentException(message);
	            } else {
	               sb.append(actualLetter);
	               if (i==0) {aD = numericValue;}
	               if (i==1) {bD = numericValue;}
	               if (i==2) {cD = numericValue;}
	               if (i==3) {dD = numericValue;}
	            }
	         }
	      }

	      res.setR(aD);
	      res.setI(bD);
	      res.setJ(cD);
	      res.setK(dD);

	      return res;
	   }

   /**
    * Regulaaravaldis mis kontrollib kas sisendi n2ol on tegu numbriga.
    * Kasutatakse meetodis valueOf() m22ramaks kas osa sisendist on number
    * @param inputData Hinnatav s6ne
    * @see http://rosettacode.org/wiki/Determine_if_a_string_is_numeric#Java
    * @author Anonymous
    * @author Urmas Hoogma
    */
   public static boolean isNumeric(String inputData) {
      return inputData.matches("[-+]?\\d+(\\.\\d+)?");
   }

   /** Clone of the quaternion.
    * @return independent clone of <code>this</code>
    */
   @Override
   public Object clone() throws CloneNotSupportedException {
      Quaternion cloned = (Quaternion)super.clone();
      cloned.setR((cloned.r));
      cloned.setI((cloned.i));
      cloned.setJ((cloned.j));
      cloned.setK((cloned.k));
      return cloned;
   }

   /** Test whether the quaternion is zero.
    * @return true, if the real part and all the imaginary parts are (close to) zero
    * @see http://stackoverflow.com/questions/1088216/whats-wrong-with-using-to-compare-floats-in-java
    */
   public boolean isZero() {
      if(Math.abs(this.r) < DELTA &&
         Math.abs(this.i) < DELTA &&
         Math.abs(this.j) < DELTA &&
         Math.abs(this.k) < DELTA){
         return true;
      } else {
         return false;
      }
   }

   /** Conjugate of the quaternion. Expressed by the formula
    *     conjugate(a+bi+cj+dk) = a-bi-cj-dk
    * @return conjugate of <code>this</code>
    */
   public Quaternion conjugate() {
      return new Quaternion(this.r,this.i*(-1),this.j*(-1),this.k*(-1));
   }

   /** Opposite of the quaternion. Expressed by the formula
    *    opposite(a+bi+cj+dk) = -a-bi-cj-dk
    * @return quaternion <code>-this</code>
    */
   public Quaternion opposite() {
      return new Quaternion(
            this.r * -1,
            this.i * -1,
            this.j * -1,
            this.k * -1);
   }

   /** Sum of quaternions. Expressed by the formula
    *    (a1+b1i+c1j+d1k) + (a2+b2i+c2j+d2k) = 
    *    (a1+a2) + (b1+b2)i + (c1+c2)j + (d1+d2)k
    * @param q addend
    * @return quaternion <code>this+q</code>
    */
   public Quaternion plus (Quaternion q) {
      return new Quaternion(
            this.r+q.getRpart(),
            this.i+q.getIpart(),
            this.j+q.getJpart(),
            this.k+q.getKpart());
   }

   /** Product of quaternions. Expressed by the formula
    *  (a1+b1i+c1j+d1k) * (a2+b2i+c2j+d2k) =
    *  (a1a2-b1b2-c1c2-d1d2) +
    *  (a1b2+b1a2+c1d2-d1c2)i +
    *  (a1c2-b1d2+c1a2+d1b2)j +
    *  (a1d2+b1c2-c1b2+d1a2)k
    * @param q factor
    * @return quaternion <code>this*q</code>
    */
   public Quaternion times (Quaternion q) {
      double a1 = this.r;
      double b1 = this.i;
      double c1 = this.j;
      double d1 = this.k;

      double a2 = ((Quaternion)q).getRpart();
      double b2 = ((Quaternion)q).getIpart();
      double c2 = ((Quaternion)q).getJpart();
      double d2 = ((Quaternion)q).getKpart();

      double r = a1*a2-b1*b2-c1*c2-d1*d2;
      double i = a1*b2+b1*a2+c1*d2-d1*c2;
      double j = a1*c2-b1*d2+c1*a2+d1*b2;
      double k = a1*d2+b1*c2-c1*b2+d1*a2;

      return new Quaternion(r, i, j, k);
   }

   /** Multiplication by a coefficient.
    * @param r coefficient
    * @return quaternion <code>this*r</code>
    */
   public Quaternion times (double r) {
      return new Quaternion(this.r*r, this.i*r, this.j*r, this.k*r);
   }

   /** Inverse of the quaternion. Expressed by the formula
    *     1/(a+bi+cj+dk) =
    *     a/(a*a+b*b+c*c+d*d) +
    *     ((-b)/(a*a+b*b+c*c+d*d))i +
    *     ((-c)/(a*a+b*b+c*c+d*d))j +
    *     ((-d)/(a*a+b*b+c*c+d*d))k
    * @return quaternion <code>1/this</code>
    */
   public Quaternion inverse() {
      if ((new Quaternion(this.r, this.i, this.j, this.k)).isZero()) {
         throw new RuntimeException("Koik argumendid ei tohi olla nullid.");
      } else {
         double r = this.r/normSquared();
         double i = this.i*-1/normSquared();
         double j = this.j*-1/normSquared();
         double k = this.k*-1/normSquared();
         return new Quaternion(r, i, j, k);
      }
   }

   /** Difference of quaternions. Expressed as addition to the opposite.
    * @param q subtrahend
    * @return quaternion <code>this-q</code>
    */
   public Quaternion minus (Quaternion q) {
      return new Quaternion(
            this.r - ((Quaternion)q).getRpart(),
            this.i - ((Quaternion)q).getIpart(),
            this.j - ((Quaternion)q).getJpart(),
            this.k - ((Quaternion)q).getKpart());
   }

   /** Right quotient of quaternions. Expressed as multiplication to the inverse.
    * @param q (right) divisor
    * @return quaternion <code>this*inverse(q)</code>
    */
   public Quaternion divideByRight (Quaternion q) {
      return this.times(q.inverse());
   }

   /** Left quotient of quaternions.
    * @param q (left) divisor
    * @return quaternion <code>inverse(q)*this</code>
    */
   public Quaternion divideByLeft (Quaternion q) {
      return q.inverse().times(this);
   }

   /** Equality test of quaternions. Difference of equal numbers
    *     is (close to) zero.
    * @param qo second quaternion
    * @return logical value of the expression <code>this.equals(qo)</code>
    */
   @Override
   public boolean equals (Object qo) {
      /** 
       * Kommentaar: See equals asi läks nüüd valesti - ma pidasin silmas, 
       * et q1.equals(q2) kehtib täpselt siis, kui q1.minus(q2).isZero()
       * Urmas Hoogma: Muutsin ära 
       * */
      if (this.minus((Quaternion) qo).isZero()) {
         return true;
      }else {
         return false;
      }
   }

   /** Dot product of quaternions. (p*conjugate(q) + q*conjugate(p))/2
    * @param q factor
    * @return dot product of this and q
    */
   public Quaternion dotMult (Quaternion q) {
      Quaternion res= (this.times(q.conjugate())).plus((q.times(this.conjugate())));
      double resNewR = res.r/2;
      res.setR(resNewR);
      return res;
   }

   /** Integer hashCode has to be the same for equal objects.
    * @return hashcode
    */
   @Override
   public int hashCode() {
      return this.toString().hashCode();
   }

   /** Norm of the quaternion. Expressed by the formula
    *     norm(a+bi+cj+dk) = Math.sqrt(a*a+b*b+c*c+d*d)
    * @return norm of <code>this</code> (norm is a real number)
    */
   public double norm() {
      return (Math.sqrt(normSquared()));
   }

   /**
    * Returns the square of the norm of the quaternion
    * Used by methods meetodites norm() and inverse()
    * @author Urmas Hoogma
    * @return square of the norm of <code>this</code>
    *    (normSquared is a real number)
    *  */
   public double normSquared() {
      return (
         Math.pow(this.r, 2)+
         Math.pow(this.i, 2)+
         Math.pow(this.j, 2)+
         Math.pow(this.k, 2));
   }

   /** Main method for testing purposes.
    * @param arg command line parameters
    */
   public static void main (String[] arg) {
      Quaternion arv1 = new Quaternion (-1., 1, 2., -2.);
      if (arg.length > 0)
         arv1 = valueOf (arg[0]);
      System.out.println ("first: " + arv1.toString());
      System.out.println ("real: " + arv1.getRpart());
      System.out.println ("imagi: " + arv1.getIpart());
      System.out.println ("imagj: " + arv1.getJpart());
      System.out.println ("imagk: " + arv1.getKpart());
      System.out.println ("isZero: " + arv1.isZero());
      System.out.println ("conjugate: " + arv1.conjugate());
      System.out.println ("opposite: " + arv1.opposite());
      System.out.println ("hashCode: " + arv1.hashCode());
      Quaternion res = null;
      try {
         res = (Quaternion)arv1.clone();
      } catch (CloneNotSupportedException e) {};
      System.out.println ("clone equals to original: " + res.equals (arv1));
      System.out.println ("clone is not the same object: " + (res!=arv1));
      System.out.println ("hashCode: " + res.hashCode());
      //res = valueOf (arv1.toString());
      System.out.println ("string conversion equals to original: "
         + res.equals (arv1));
      Quaternion arv2 = new Quaternion (1., -2.,  -1., 2.);
      if (arg.length > 1)
         arv2 = valueOf (arg[1]);
      System.out.println ("second: " + arv2.toString());
      System.out.println ("hashCode: " + arv2.hashCode());
      System.out.println ("equals: " + arv1.equals (arv2));
      res = arv1.plus (arv2);
      System.out.println ("plus: " + res);
      System.out.println ("times: " + arv1.times (arv2));
      System.out.println ("minus: " + arv1.minus (arv2));
      double mm = arv1.norm();
      System.out.println ("norm: " + mm);
      System.out.println ("inverse: " + arv1.inverse());
      System.out.println ("divideByRight: " + arv1.divideByRight (arv2));
      System.out.println ("divideByLeft: " + arv1.divideByLeft (arv2));
      System.out.println ("dotMult: " + arv1.dotMult (arv2));

      System.out.println("Minu hashcode "+(new Quaternion(1000.0, 1000.0, 1000.0, 1000.0)).hashCode());

      // Input that will throw an IllegalArgumentException
      String v22r = "1.0+-2.0i+.0j+2.0k";
      // Input that is valid
      String ok = "1.0+-2.0i+-1.0j+2.0k";
      System.out.println(ok);
      //valueOf(v22r);

      Quaternion oneNull = new Quaternion(0.,0.,0.,0.);
      Quaternion otherNull = new Quaternion(0.,0.,0.,0.);
      Quaternion notNull = new Quaternion(0.,0.,0.,0.1);
      System.out.println(oneNull.equals(otherNull)); // true
      System.out.println(valueOf("0+-1i2j+-3k")); // false
   }
}
// end of file
