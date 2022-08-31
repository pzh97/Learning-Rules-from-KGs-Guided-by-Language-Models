import torch
from transformers import BertTokenizer, BertModel, BertForMaskedLM
import re

class Model:
    def __init__(self):
        self.tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
        self.model = BertForMaskedLM.from_pretrained('bert-base-uncased')
        self.model.eval()

    def predict(self, text, top_k = 100):
        self.text = "[CLS] %s [SEP]"%text
        self.tokenized_text = self.tokenizer.tokenize(self.text)
        self.masked_index = self.tokenized_text.index("[MASK]")
        self.indexed_tokens = self.tokenizer.convert_tokens_to_ids(self.tokenized_text)
        self.tokens_tensor = torch.tensor([self.indexed_tokens])

        with torch.no_grad():
            outputs = self.model(self.tokens_tensor)
            predictions = outputs[0]

        
        probs = torch.nn.functional.softmax(predictions[0, self.masked_index], dim = -1)
        top_k_weights, top_k_indices = torch.topk(probs, top_k, sorted = True)
        output = []
        for i, pred_idx in enumerate(top_k_indices):
            predicted_token = self.tokenizer.convert_ids_to_tokens([pred_idx])[0]
            token_weight = top_k_weights[i]
            output.append({"prediction": predicted_token, "probability": float(token_weight)})
        return output

#model = Model()
#print(model.predict("Dante was born in  [MASK].", top_k=10))


