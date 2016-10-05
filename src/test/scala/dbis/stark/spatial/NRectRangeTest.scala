package dbis.stark.spatial

import org.scalatest.FlatSpec
import org.scalatest.Matchers


import org.scalacheck.Properties
import org.scalacheck.Gen
import org.scalacheck.Arbitrary

import org.scalacheck.Prop.{forAll, BooleanOperators}

class NPointCheck extends Properties("NPoint") {
  
  val pointGen = for {
    x <- Gen.choose(-180,180)
    y <- Gen.choose(-90, 90)
  } yield NPoint(x,y)
  implicit lazy val arbPoint: Arbitrary[NPoint] = Arbitrary(pointGen)
  

  
}

class NRectRangeCheck extends Properties("NRectRange") {
    
  
  
  val rectGen = for {
    x1 <- Gen.choose(-180,178)
    y1 <- Gen.choose(-90, 88)
    x2 <- Gen.choose(x1+1,180)
    y2 <- Gen.choose(y1+1, 90)
  } yield NRectRange(NPoint(x1,y1), NPoint(x2,y2))

  implicit lazy val arbRect: Arbitrary[NRectRange] = Arbitrary(rectGen)
  
  
  property("contains") = forAll { (r: NRectRange) => 
      r.contains(r) 
  }
  
  property("contains own ll") = forAll { (r: NRectRange) => 
    r.contains(r.ll)  
  }
  
  property("not contain own ur") = forAll { (r: NRectRange) => 
    !r.contains(r.ur)  
  }
  
  property("extend") = forAll { (r1: NRectRange, r2: NRectRange) =>
    
    val llX = if(r1.ll(0) < r2.ll(0)) r1.ll(0) else r2.ll(0)
    val llY = if(r1.ll(1) < r2.ll(1)) r1.ll(1) else r2.ll(1)
    val urX = if(r1.ur(0) > r2.ur(0)) r1.ur(0) else r2.ur(0)
    val urY = if(r1.ur(1) > r2.ur(1)) r1.ur(1) else r2.ur(1)
    
    val expExtend = NRectRange(NPoint(llX,llY),NPoint(urX, urY))
    
    val extend = r1.extend(r2)
    extend == expExtend
  }
}

class NRectRangeTest extends FlatSpec with Matchers {
  
  
  "A NRectRange" should "correctly contain a point" in {
    
    val rect = NRectRange(NPoint(0, 0), NPoint(10, 10))
    
		rect.contains(NPoint(5,5)) shouldBe true
    rect.contains(NPoint(0,0)) shouldBe true
    rect.contains(NPoint(0,5)) shouldBe true
    rect.contains(NPoint(0,10)) shouldBe false
    rect.contains(NPoint(5,0)) shouldBe true
    rect.contains(NPoint(9,0)) shouldBe true
    rect.contains(NPoint(10,0)) shouldBe false
    rect.contains(NPoint(10,10)) shouldBe false
  }
  
  it should "contain itself"  in {
    
    val rect = NRectRange(NPoint(0, 0), NPoint(10, 10))
    
    rect.contains(rect) shouldBe true
  }
  
  it should "correctly extend" in {
    
    val rect = NRectRange(NPoint(3, 3), NPoint(10, 10))
    
    
    val rect2 = NRectRange(NPoint(1, 5), NPoint(7, 14))
    
    val ex = rect.extend(rect2)
    
    ex shouldBe NRectRange(NPoint(1,3), NPoint(10,14))
    
  }
  
  it should "not extend for a smaller rect" in {
    val rect = NRectRange(NPoint(3, 3), NPoint(10, 10))
    
    
    val rect2 = NRectRange(NPoint(4, 5), NPoint(7, 8))
    
    val ex = rect.extend(rect2)
    
    ex shouldBe rect
  }
  
  it should "have the correct length" in {
    
    val rect = NRectRange(NPoint(0,0), NPoint(3,3))
    val lengths = rect.lengths
    
    lengths.size shouldBe 2
    lengths should contain only (3)
    
  }
  
  it should "have the correct length with negative ll" in {
    
    val rect = NRectRange(NPoint(-1,-1), NPoint(3,3))
    val lengths = rect.lengths
    
    lengths.size shouldBe 2
    lengths should contain only (4)
    
  }
  
  it should "have the correct length with both ll ur negative" in {
    
    val rect = NRectRange(NPoint(-4,-4), NPoint(-1,-1))
    val lengths = rect.lengths
    
    lengths.size shouldBe 2
    lengths should contain only (3)
    
  }
  
  it should "create the correct diff range for changing x" in {
    
    val rect1 = NRectRange(NPoint(-14,-14), NPoint(-7,-7))
    val rect2 = NRectRange(NPoint(-14,-14), NPoint(-1,-7))
    
    val diff = rect1.diff(rect2)
    diff shouldBe NRectRange(NPoint(-7,-14), NPoint(-1,-7))
    
  }
  
  it should "create the correct diff range for changing y" in {
    
    val rect1 = NRectRange(NPoint(-14,-14), NPoint(-7,-1))
    val rect2 = NRectRange(NPoint(-14,-14), NPoint(-7,-7))
    
    val diff = rect1.diff(rect2)
    diff shouldBe NRectRange(NPoint(-14,-7), NPoint(-7,-1))
    
  }
  
  it should "diff should be commutative" in {
    
    val rect1 = NRectRange(NPoint(-14,-14), NPoint(-7,-7))
    val rect2 = NRectRange(NPoint(-14,-14), NPoint(-1,-7))
    
    val diff = rect1.diff(rect2)
    val diff2 = rect2.diff(rect1)
    diff shouldBe diff2
  }
  
}