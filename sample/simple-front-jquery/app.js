let express = require('express');
let app = express();
app.use(express.static('src', {'index': 'index.html'}));

// app.get('/api/hello', function (req, res) {
//     res.send('Hello World!');
// });


app.listen(8600);