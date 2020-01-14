function buildGraph() {

    var graphJson = JSON.parse(document.getElementById('graphJson').innerHTML)

    var cy = cytoscape({

        container: document.getElementById('cy'), // container to render in

        elements: graphJson,

        style: [ // the stylesheet for the graph
            {
                selector: 'node',
                style: {
                    'background-color': '#666',
                    'label': 'data(id)'
                }
            },

            {
                selector: 'edge',
                style: {
                    'width': 3,
                    'line-color': '#ccc',
                    'target-arrow-color': '#ccc',
                    'target-arrow-shape': 'triangle'
                }
            }
        ],

        layout: {
            name: 'random',
            rows: 1
        }

    });

}