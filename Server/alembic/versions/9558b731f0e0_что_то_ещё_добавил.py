"""Что-то ещё добавил

Revision ID: 9558b731f0e0
Revises: da689d320e63
Create Date: 2020-04-05 00:13:40.848328

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '9558b731f0e0'
down_revision = 'da689d320e63'
branch_labels = None
depends_on = None


def upgrade():
    pass
    # ### commands auto generated by Alembic - please adjust! ###
    # op.drop_constraint(None, 'messages', type_='foreignkey')
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_foreign_key(None, 'messages', 'users', ['to'], ['id'])
    # ### end Alembic commands ###
