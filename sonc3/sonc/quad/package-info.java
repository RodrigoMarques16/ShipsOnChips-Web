/**
 * A quad tree for storing points, objects with a pair of x and x coordinates.
 * The former grow to the right and the later grow up, as in a two-dimensional
 * referential in mathematics. This package is designed to be reusable in
 * different applications. Any type implementing the HasPoint interface can be
 * stored in these quad trees.
 * 
 * The quad trees in this package follow the <b>Composite</b> design pattern to
 * create a common type Trie (component), specialized as as LeafTrie (leaf) and
 * NodeTrie (composite), of which PointQuadtree is the client.
 * 
 * The PointQuadtree class acts as a <b>Facade</b> to this package, following
 * the design pattern with that name, since it provides a single access point to
 * quad trees
 * 
 * @author Afonso Brandão
 *
 */
package sonc.quad;