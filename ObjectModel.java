import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import org.ejml.simple.SimpleMatrix;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
public class ObjectModel {
  // The set abstraction is used, because theoretically two equal vertices does nothing.
  private String name;
  // vertices
  private SimpleMatrix verticesMatrix;
  private Set<Point> vertices;
  // vertex normals
  private SimpleMatrix vertexNormalsMatrix;  
  private Set<Point> vertex_normals;
  private Set<String> faces;
  private Boolean smoothing;
  private String tempSaveSmoothing = "";
  private List<String> commentBlock;

  /**
   * The single string constructor will initialize the ObjectModel from a *.obj file.
   */
  public ObjectModel(String fname){
    name = "default";
    smoothing = false;
    vertices = new LinkedHashSet<Point>();
    vertex_normals = new LinkedHashSet<Point>();
    faces = new LinkedHashSet<String>();
    commentBlock = new ArrayList<String>(2);    
    InitObjectFromFile(fname, this); // Will side effect state of 'this'
  }

  public String toString() {
    String str = this.name + ":\n" ;
    str += "Vertices:\n";
    str += verticesMatrix.toString();
    str += "Vertex Normals:\n";
    str += vertexNormalsMatrix.toString();
    str += "Faces:\n";
    for (String s : this.faces) {
      str += "\t" + s + "\n";
    }
    str += "Smoothing: '" + this.smoothing.toString() + "'\n";
    return str;
  }
  private static void InitObjectFromFile(String fname, ObjectModel newObject){
    // cube.obj --> ["cube", "obj"]
    newObject.name = fname;
    try {
      Scanner fReader = new Scanner(new File(fname));
      int lineCount = 0;
      // Maintain the comment block
      while (fReader.hasNext("#*")) {
        String commentLine = fReader.nextLine();
        newObject.commentBlock.add(commentLine);
      }
      // Parse file
      while (fReader.hasNext()) {
        lineCount++;
        String line = fReader.nextLine();
        // Lines starting with '#' are comments
        if (!line.startsWith("#")) {
          String [] lineItems = line.split(" ");
          // Line is a vertex or vertex normal
          // v 0.000000 4.000000 4.000000
          // vn 0.0000 0.0000 1.0000
          if (lineItems[0].equals("v") || lineItems[0].equals("vn")) {
            double x = Double.parseDouble(lineItems[1]);
            double y = Double.parseDouble(lineItems[2]);
            double z = Double.parseDouble(lineItems[3]);
            Point vert = new Point(x, y, z);
            // Add vert to vertices, or vertex normals depending on line designator
            if (lineItems[0].equals("v")) {
              newObject.vertices.add(vert);                
            } else {
              newObject.vertex_normals.add(vert);
            }
          } else if (lineItems[0].equals("f")) {
            // f 2//1 4//1 1//1
            // for now ignore face parsing
            newObject.faces.add(line);
          } else if (lineItems[0].equals("s")) {
            // I have no idea what the 's' does right now
            newObject.tempSaveSmoothing = line;
            // newObject.smoothing = false; ??
          } else {
            // unrecognized symbol, ignore
            System.err.printf("Unrecognized symbol '%s' in '%s'\n\tLine %d: %s", lineItems[0], fname, lineCount, line);
          }
        }
      }
      fReader.close();
      // Convert the vertex sets to matrices
      newObject.verticesMatrix = pointSetToMatrix(newObject.vertices);
      newObject.vertexNormalsMatrix = pointSetToMatrix(newObject.vertex_normals);
      return;
    } catch (FileNotFoundException e) {
      System.err.printf("The file '%s' does not exist", fname);
      newObject = null;
      return;
    }
  }

  /**
   * | x ... 0 |
   * | y ... 0 |
   * | z ... 0 |
   * | 0 ... 1 |
   */
  private static SimpleMatrix pointSetToMatrix(Set<Point> PointSet) {
    // Columns are each vertex, rows are x,y,z
    double[][] data = new double[4][PointSet.size()];
    int i = 0;
    for (Point t : PointSet) {
      // Init the x, y, and z coordinates
      data[0][i] = t.x;
      data[1][i] = t.y;
      data[2][i] = t.z;
      data[3][i] = 1;
      // maybe add an m for homogenous coordinates?
      i++;
    }
    return new SimpleMatrix(data);
  }

  public void writeToFile(String filename) throws Exception {
    PrintWriter outputWriter = new PrintWriter(new File(filename));
    for (String s : commentBlock) {
      outputWriter.println(s);
    }
    outputWriter.println("# Modified files written by a cs410 class project");
    outputWriter.println("# Author -- Brandt Reutimann");
    // Write the vertices out
    for (int i = 0; i < vertices.size(); i++) {
      double x = verticesMatrix.get(0, i);
      double y = verticesMatrix.get(1, i);
      double z = verticesMatrix.get(2, i);
      outputWriter.printf("v %.6f %.6f %.6f\n",x,y,z);
    }
    // write the vertex normals out
    // for (Point p : vertex_normals) {
    //   outputWriter.printf("vn %.4f %.4f %.4f\n", p.x, p.y, p.z);
    // }
    // write out the smoothing
    // outputWriter.printf("%s\n", tempSaveSmoothing);
    // Write out the faces
    for (String f : faces) {
      outputWriter.println(f);
    }
    outputWriter.close();
  }
  public SimpleMatrix getVerticesMatrix () {
    return this.verticesMatrix;
  }

  public void setVerticesMatrix (SimpleMatrix toSet) {
    this.verticesMatrix = toSet;
  }
}