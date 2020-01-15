function buildGraph() {

    var graphJson = JSON.parse(document.getElementById('graphJson').innerHTML)

    var cy = cytoscape({

        container: document.getElementById('cy'),

        elements: graphJson,

        style: [
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
                    'target-arrow-shape': 'triangle',
                    'curve-style': 'bezier'
                }
            }
        ],

        layout: {
            name: 'cose-bilkent',
            nodeDimensionsIncludeLabels: true,
            idealEdgeLength: 100
        },

        userZoomingEnabled: false

    });

    cy.on('tap', 'node', function () {
        try {
            window.open(this.data('href'));
        } catch (e) {
            window.location.href = this.data('href');
        }
    });

}

