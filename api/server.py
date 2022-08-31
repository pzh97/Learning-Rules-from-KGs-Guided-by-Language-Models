import json
import logging
import os
import queue
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer, HTTPServer
from socketserver import ThreadingMixIn
from urllib.parse import urlparse, parse_qs
from model import Model

class ThreadedHTTPServer(ThreadingMixIn, HTTPServer):
    pass


queue = queue.Queue()
for item in range(4):
    model = Model()
    queue.put(model)


class Handler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args,
                         directory=os.path.dirname(os.path.realpath(__file__)),
                         **kwargs)

    def _set_response(self):
        self.send_response(200)
        self.send_header('Content-Type', 'application/json')
        self.end_headers()

    def do_GET(self):
        url = urlparse(self.path)
        if url.path == '/predict':
            model = queue.get()
            params = parse_qs(url.query)
            c = params['query'][0]
            response = model.predict(c, top_k=100)
            self._set_response()
            self.wfile.write("{}".format(json.dumps(response)).encode('utf-8'))
            queue.put(model)
        else:
            super().do_GET()

def run(port=6993):
    logging.basicConfig(level=logging.INFO)
    server_address = ('0.0.0.0', port)
    httpd = ThreadingHTTPServer(server_address, Handler)
    logging.info('Starting httpd...\n')
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()
    logging.info('Stopping httpd...\n')


if __name__ == '__main__':
    run(6993)
