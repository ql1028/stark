name := "SpatialSpark"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
   "com.vividsolutions" % "jts" % "1.13",
   "org.apache.spark" % "spark-core_2.11" % "1.6.0" 
)
