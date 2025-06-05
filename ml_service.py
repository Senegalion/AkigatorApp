import pandas as pd
import numpy as np
import random
import argparse
import os
from collections import Counter
from sqlalchemy import create_engine, text

# ---------------- CONFIG ---------------- #
DB_USER = 'postgres'
DB_PASSWORD = 'postgres'
DB_HOST = 'localhost'
DB_PORT = '5431'
DB_NAME = 'akigator_db'
CATEGORY_ID = 2
NUM_OPTIONS = 5
DATASET_PATH = os.path.join(os.path.dirname(__file__), "Animal Dataset.csv")

engine = create_engine(
    f'postgresql+psycopg2://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}'
)

df = pd.read_csv(DATASET_PATH)
target_column = "Animal"
features = [col for col in df.columns if col != target_column]

def entropy(y):
    counts = Counter(y)
    probabilities = [count / len(y) for count in counts.values()]
    return -sum(p * np.log2(p) for p in probabilities if p > 0)

def information_gain(data, feature, target):
    total_entropy = entropy(data[target])
    values = data[feature].dropna().unique()
    weighted_entropy = sum(
        (len(data[data[feature] == v]) / len(data)) * entropy(data[data[feature] == v][target])
        for v in values
    )
    return total_entropy - weighted_entropy

def select_best_question(data, features, target):
    gains = {feature: information_gain(data, feature, target) for feature in features}
    return max(gains, key=gains.get)


# ----------- DATABASE ACTIONS ---------- #
def clear_database():
    """Czyści dane z tabeli answers przed nowym pytaniem."""
    with engine.begin() as conn:
        conn.execute(text("DELETE FROM answers"))
        conn.execute(text("DELETE FROM questions"))

def insert_question_and_get_id(question_text):
    with engine.begin() as conn:
        result = conn.execute(
            text("""
                INSERT INTO questions (category_id, content)
                VALUES (:category_id, :content)
                RETURNING question_id
            """),
            {"category_id": CATEGORY_ID, "content": question_text}
        )
        return result.scalar()

def insert_answers(question_id, answers_list):
    with engine.begin() as conn:
        previous_question_id = question_id - 1
        conn.execute(text("DELETE FROM answers WHERE question_id = :previous_question_id"), {"previous_question_id": previous_question_id})
        for answer_text in answers_list:
            conn.execute(
                text("""
                    INSERT INTO answers (question_id, entity_id, response)
                    VALUES (:question_id, NULL, :response)
                """),
                {"question_id": question_id, "response": answer_text}
            )

def get_answer_info(answer_id):
    """Pobierz treść odpowiedzi i treść pytania."""
    with engine.begin() as conn:
        result = conn.execute(
            text("""
                SELECT a.response AS answer_text, q.content AS question_text
                FROM answers a
                JOIN questions q ON a.question_id = q.question_id
                WHERE a.answer_id = :answer_id
            """),
            {"answer_id": answer_id}
        ).fetchone()
        return result if result else None

# -------------- GAME LOGIC ------------- #
def akinator_step(mode, answer_id=None):
    current_data = df.copy()

    if mode == "start":
        clear_database()  # Wyczyszczenie tabeli przed rozpoczęciem nowej gry
        # Pierwsze pytanie jest stałe
        question_attr = "Color"
        values = list(current_data[question_attr].dropna().unique())
        choices = random.sample(values, min(NUM_OPTIONS, len(values))) + ["No answer"]

        question_id = insert_question_and_get_id(f"What is the color of the animal?")
        insert_answers(question_id, choices)  # Pierwsze pytanie, brak poprzedniego pytania
        print(f"QUESTION_ID={question_id}")

    elif mode == "next":
        if not answer_id:
            raise ValueError("Missing --answer_id")

        answer_info = get_answer_info(answer_id)
        if not answer_info:
            raise ValueError(f"No answer found for ID {answer_id}")

        answer_text, question_text = answer_info

        # Wyciągnij cechę z pytania (np. "What is the color of the animal?" → "Color")
        matching_feature = None
        for feature in features:
            if feature.lower() in question_text.lower():
                matching_feature = feature
                break

        if not matching_feature:
            raise ValueError(f"Could not recognize feature from question: {question_text}")

        # Filtruj dane
        if answer_text != "No answer":
            current_data = current_data[current_data[matching_feature] == answer_text]

        if len(current_data[target_column].unique()) == 1:
            print(f"GUESS={current_data[target_column].iloc[0]}")
        elif len(current_data) == 0:
            print("GUESS=None")
        else:
            remaining_features = [f for f in features if f != matching_feature]
            if not remaining_features:
                print("GUESS=None")  # Brak więcej pytań
            else:
                next_q = select_best_question(current_data, remaining_features, target_column)
                values = list(current_data[next_q].dropna().unique())
                choices = random.sample(values, min(NUM_OPTIONS, len(values))) + ["No answer"]

                # Teraz poprzedni question_id to current question_id - 1
                question_id = insert_question_and_get_id(f"What is the {next_q.lower()} of the animal?")
                insert_answers(question_id, choices)  # Teraz używamy current question_id
                print(f"QUESTION_ID={question_id}")
    else:
        raise ValueError("Unknown mode")


# ---------------- ENTRY ---------------- #
if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--mode", required=True, choices=["start", "next"])
    parser.add_argument("--answer_id", type=int)
    args = parser.parse_args()

    akinator_step(args.mode, args.answer_id)
