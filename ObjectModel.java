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
  private List<Point> vertices;
  // vertex normals
  private SimpleMatrix vertexNormalsMatrix;  
  private List<Point> vertex_normals;
  private List<Face> faces;
  private Boolean smoothing;
  private String tempSaveSmoothing = "";
  private List<String> commentBlock;

  /**
   * The single string constructor will initialize the ObjectModel from a *.obj file.
   */
  public ObjectModel(String fname){
    name = "default";
    smoothing = false;
    vertices = new ArrayList<Point>();
    vertex_normals = new ArrayList<Point>();
    faces = new ArrayList<Face>();
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
    for (Face f : this.faces) {
      str += "\n" + f + "\n";
    }
    str += "Smoothing: '" + this.smoothing.toString() + "'\n";
    return str;
  }

  /**
   * Loads a wavefront obj file into the object model
   */
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
            // Get the indices for each vertex that makes up the face
            int av_index = Integer.parseInt(lineItems[1].split("//")[0]);
            int bv_index = Integer.parseInt(lineItems[2].split("//")[0]);
            int cv_index = Integer.parseInt(lineItems[3].split("//")[0]);
            // Save a list to back track what the indices are when you write the file back out
            int [] indices = {av_index, bv_index, cv_index};
            // Get the point for each vertex index, have to do -1 because the file is 1 indexed instead of 0 indexed
            Point av = newObject.vertices.get(av_index - 1);
            Point bv = newObject.vertices.get(bv_index - 1);
            Point cv = newObject.vertices.get(cv_index - 1);
            // Create new face object and it to the list
            newObject.faces.add(new Face(av, bv, cv, indices));
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
  private static SimpleMatrix pointSetToMatrix(List<Point> PointSet) {
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

  /**
   * Deprecated now, won't be able to write out faces. Have to back track the indexing
   */
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
    for (Face f : faces) {
      outputWriter.printf("f %d %d %d\n", f.vertexIndices[0], f.vertexIndices[1], f.vertexIndices[2]);
    }
    outputWriter.close();
  }
  public SimpleMatrix getVerticesMatrix () {
    return this.verticesMatrix;
  }

  public void setVerticesMatrix (SimpleMatrix toSet) {
    this.verticesMatrix = toSet;
  }

  public List<Face> getFaces() {
    return this.faces;
  }
}